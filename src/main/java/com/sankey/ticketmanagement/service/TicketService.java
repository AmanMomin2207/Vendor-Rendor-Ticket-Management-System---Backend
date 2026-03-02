package com.sankey.ticketmanagement.service;

import com.sankey.ticketmanagement.model.*;
import com.sankey.ticketmanagement.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository,
                         TicketHistoryRepository historyRepository,
                         UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    // 🔹 BUYER creates ticket
    public Ticket createTicket(String title, String description, Priority priority) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .status(TicketStatus.OPEN)
                .createdBy(buyer.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ticketRepository.save(ticket);
    }

    // 🔹 ADMIN assigns ticket
    public Ticket assignTicket(String ticketId, String vendorId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new RuntimeException("Only OPEN tickets can be assigned");
        }

        TicketStatus oldStatus = ticket.getStatus();

        ticket.setAssignedTo(vendorId);
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);

        saveHistory(ticketId, oldStatus, TicketStatus.ASSIGNED);

        return updated;
    }

    // 🔹 VENDOR updates status
    public Ticket updateStatus(String ticketId, TicketStatus newStatus) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        TicketStatus current = ticket.getStatus();

        validateStatusTransition(current, newStatus);

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        Ticket updated = ticketRepository.save(ticket);

        saveHistory(ticketId, current, newStatus);

        return updated;
    }

    // 🔹 BUYER closes ticket
    public Ticket closeTicket(String ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new RuntimeException("Only RESOLVED tickets can be closed");
        }

        TicketStatus oldStatus = ticket.getStatus();

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);

        saveHistory(ticketId, oldStatus, TicketStatus.CLOSED);

        return updated;
    }

    // 🔹 Status validation rules
    private void validateStatusTransition(TicketStatus current, TicketStatus next) {

        if (current == TicketStatus.ASSIGNED && next == TicketStatus.IN_PROGRESS) return;
        if (current == TicketStatus.IN_PROGRESS && next == TicketStatus.RESOLVED) return;

        throw new RuntimeException("Invalid status transition");
    }

    // 🔹 Save history automatically
    private void saveHistory(String ticketId,
                             TicketStatus oldStatus,
                             TicketStatus newStatus) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);

        TicketHistory history = TicketHistory.builder()
                .ticketId(ticketId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(user != null ? user.getId() : null)
                .changedAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }
}
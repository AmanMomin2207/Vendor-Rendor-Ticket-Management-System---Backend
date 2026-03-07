package com.sankey.ticketmanagement.service;

import com.sankey.ticketmanagement.dto.PagedResponse;
import com.sankey.ticketmanagement.exception.BadRequestException;
import com.sankey.ticketmanagement.exception.ResourceNotFoundException;
import com.sankey.ticketmanagement.exception.UnauthorizedException;
import com.sankey.ticketmanagement.model.*;
import com.sankey.ticketmanagement.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;
import java.util.List;

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

    // Only ADMIN get all tickets
    public Ticket getTicketById(String id, String email, String role) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (role.equals("ADMIN")) return ticket;

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (role.equals("BUYER") && !ticket.getCreatedBy().equals(user.getId())) {
            throw new UnauthorizedException("Access denied");
        }

        if (role.equals("VENDOR") && !user.getId().equals(ticket.getAssignedTo())) {
            throw new UnauthorizedException("Access denied");
        }

        return ticket;
    }

    // 🔹 ADMIN assigns ticket
    public Ticket assignTicket(String ticketId, String vendorId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new BadRequestException("Only RESOLVED tickets can be closed");
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
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new BadRequestException("Only RESOLVED tickets can be closed");
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

        throw new BadRequestException("Invalid status transition");
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

    public PagedResponse<Ticket> getTickets(
            int page,
            int size,
            TicketStatus status,
            Priority priority,
            String search,
            String sortBy,
            String direction,
            String email) {

        Sort sort = direction.equalsIgnoreCase("asc") ?
        Sort.by(sortBy).ascending() :
        Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Ticket> ticketPage;

        if (user.getRole().name().equals("ADMIN")) {

            ticketPage = applyFilters(status, priority, search, pageable);

        } else if (user.getRole().name().equals("BUYER")) {

            ticketPage = ticketRepository
                    .findByCreatedBy(user.getId(), pageable);

        } else if (user.getRole().name().equals("VENDOR")) {

            ticketPage = ticketRepository
                    .findByAssignedTo(user.getId(), pageable);

        } else {
            throw new RuntimeException("Invalid role");
        }

        return new PagedResponse<>(
                ticketPage.getContent(),
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages(),
                ticketPage.isLast()
        );
    }

    private Page<Ticket> applyFilters(
            TicketStatus status,
            Priority priority,
            String search,
            Pageable pageable) {

        if (status != null && search != null) {
            return ticketRepository
                    .findByStatusAndTitleContainingIgnoreCase(status, search, pageable);
        }

        if (status != null) {
            return ticketRepository.findByStatus(status, pageable);
        }

        if (priority != null) {
            return ticketRepository.findByPriority(priority, pageable);
        }

        if (search != null) {
            return ticketRepository.findByTitleContainingIgnoreCase(search, pageable);
        }

        return ticketRepository.findAll(pageable);
    }

    public ByteArrayInputStream exportTicketsToCSV() {

        List<Ticket> tickets = ticketRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        // CSV Header
        writer.println("ID,Title,Description,Priority,Status,CreatedBy,AssignedTo,CreatedAt");

        for (Ticket ticket : tickets) {
            writer.println(
                    ticket.getId() + "," +
                    ticket.getTitle() + "," +
                    ticket.getDescription() + "," +
                    ticket.getPriority() + "," +
                    ticket.getStatus() + "," +
                    ticket.getCreatedBy() + "," +
                    ticket.getAssignedTo() + "," +
                    ticket.getCreatedAt()
            );
        }

        writer.flush();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public List<TicketHistory> getTicketHistory(String ticketId) {
        return historyRepository.findByTicketIdOrderByChangedAtAsc(ticketId);
    }
}
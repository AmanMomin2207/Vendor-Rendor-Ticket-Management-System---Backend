package com.sankey.ticketmanagement.service;

import com.sankey.ticketmanagement.dto.VendorStatsResponse;
import com.sankey.ticketmanagement.model.*;
import com.sankey.ticketmanagement.repository.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorStatsService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public VendorStatsService(UserRepository userRepository,
                               TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<VendorStatsResponse> getAllVendorStats() {

        List<User> vendors = userRepository.findByRole(Role.VENDOR);

        return vendors.stream().map(vendor -> {

            List<Ticket> allTickets = ticketRepository
                    .findByAssignedTo(vendor.getId());

            long total = allTickets.size();

            long inProgress = allTickets.stream()
                    .filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS)
                    .count();

            long resolved = allTickets.stream()
                    .filter(t -> t.getStatus() == TicketStatus.RESOLVED)
                    .count();

            long closed = allTickets.stream()
                    .filter(t -> t.getStatus() == TicketStatus.CLOSED)
                    .count();

            double resolutionRate = total > 0
                    ? Math.round(((double)(resolved + closed) / total) * 100.0)
                    : 0;

            // avg resolution time from assignedAt to resolvedAt
            String avgTime = calculateAvgResolutionTime(allTickets);

            return new VendorStatsResponse(
                    vendor.getId(),
                    vendor.getName(),
                    vendor.getEmail(),
                    total,
                    inProgress,
                    resolved,
                    closed,
                    resolutionRate,
                    avgTime
            );

        }).collect(Collectors.toList());
    }

    private String calculateAvgResolutionTime(List<Ticket> tickets) {

        List<Ticket> resolvedTickets = tickets.stream()
                .filter(t -> t.getResolvedAt() != null && t.getCreatedAt() != null)
                .collect(Collectors.toList());

        if (resolvedTickets.isEmpty()) return "—";

        long totalMinutes = resolvedTickets.stream()
                .mapToLong(t -> Duration.between(
                        t.getCreatedAt(),
                        t.getResolvedAt()
                ).toMinutes())
                .sum();

        long avgMinutes = totalMinutes / resolvedTickets.size();

        long days = avgMinutes / (60 * 24);
        long hours = (avgMinutes % (60 * 24)) / 60;
        long minutes = avgMinutes % 60;

        if (days > 0) return days + "d " + hours + "h";
        if (hours > 0) return hours + "h " + minutes + "m";
        return minutes + "m";
    }
}
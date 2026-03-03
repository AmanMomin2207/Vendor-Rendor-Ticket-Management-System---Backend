package com.sankey.ticketmanagement.service;

import com.sankey.ticketmanagement.dto.DashboardResponse;
import com.sankey.ticketmanagement.model.TicketStatus;
import com.sankey.ticketmanagement.model.User;
import com.sankey.ticketmanagement.repository.TicketRepository;
import com.sankey.ticketmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public DashboardService(TicketRepository ticketRepository,
                            UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public DashboardResponse getBuyerDashboardByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String buyerId = user.getId();

        long total = ticketRepository.countBycreatedBy(buyerId);
        long open = ticketRepository.countBycreatedByAndStatus(buyerId, TicketStatus.OPEN);
        long closed = ticketRepository.countBycreatedByAndStatus(buyerId, TicketStatus.CLOSED);

        return new DashboardResponse(total, open, 0, 0, closed);
    }

    public DashboardResponse getAdminDashboard() {

        long total = ticketRepository.count();
        long open = ticketRepository.countByStatus(TicketStatus.OPEN);
        long inProgress = ticketRepository.countByStatus(TicketStatus.IN_PROGRESS);
        long resolved = ticketRepository.countByStatus(TicketStatus.RESOLVED);
        long closed = ticketRepository.countByStatus(TicketStatus.CLOSED);

        return new DashboardResponse(total, open, inProgress, resolved, closed);
    }

    public DashboardResponse getVendorDashboardByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String vendorId = user.getId();

        long total = ticketRepository.countByAssignedTo(vendorId);
        long inProgress = ticketRepository.countByAssignedToAndStatus(vendorId, TicketStatus.IN_PROGRESS);
        long resolved = ticketRepository.countByAssignedToAndStatus(vendorId, TicketStatus.RESOLVED);

        return new DashboardResponse(total, 0, inProgress, resolved, 0);
    }
}
package com.sankey.ticketmanagement.service;

import com.sankey.ticketmanagement.dto.DashboardResponse;
import com.sankey.ticketmanagement.model.TicketStatus;
import com.sankey.ticketmanagement.repository.TicketRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final TicketRepository ticketRepository;

    public DashboardService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public DashboardResponse getBuyerDashboard(String buyerId) {

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

    public DashboardResponse getVendorDashboard(String vendorId) {

        long total = ticketRepository.countByAssignedTo(vendorId);
        long inProgress = ticketRepository.countByAssignedToAndStatus(vendorId, TicketStatus.IN_PROGRESS);
        long resolved = ticketRepository.countByAssignedToAndStatus(vendorId, TicketStatus.RESOLVED);

        return new DashboardResponse(total, 0, inProgress, resolved, 0);
    }
}
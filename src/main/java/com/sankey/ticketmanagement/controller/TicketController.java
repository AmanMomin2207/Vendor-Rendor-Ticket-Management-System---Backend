package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.*;
import com.sankey.ticketmanagement.model.Ticket;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.service.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin("*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Buyer only
    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    public ApiResponse<Ticket> create(@RequestBody CreateTicketRequest request) {
        Ticket ticket = ticketService.createTicket(request);
        return new ApiResponse<>(true, "Ticket created successfully", ticket);
    }

    // Admin only
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/assign")
    public ApiResponse<Ticket> assign(@PathVariable String id,
                                      @RequestBody AssignTicketRequest request) {
        Ticket ticket = ticketService.assignTicket(id, request.getVendorId());
        return new ApiResponse<>(true, "Ticket assigned successfully", ticket);
    }

    // Vendor only
    @PreAuthorize("hasRole('VENDOR')")
    @PutMapping("/{id}/status")
    public ApiResponse<Ticket> updateStatus(@PathVariable String id,
                                            @RequestBody UpdateStatusRequest request) {
        Ticket ticket = ticketService.updateStatus(id, request.getStatus());
        return new ApiResponse<>(true, "Status updated successfully", ticket);
    }

    // Buyer only
    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{id}/close")
    public ApiResponse<Ticket> close(@PathVariable String id) {
        Ticket ticket = ticketService.closeTicket(id);
        return new ApiResponse<>(true, "Ticket closed successfully", ticket);
    }
}
package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.*;
import com.sankey.ticketmanagement.model.Ticket;
import com.sankey.ticketmanagement.service.TicketService;
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
    @PostMapping("/create")
    public Ticket create(@RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(
                request.getTitle(),
                request.getDescription(),
                request.getPriority());
    }

    // Admin only
    @PutMapping("/{id}/assign")
    public Ticket assign(@PathVariable String id,
                         @RequestBody AssignTicketRequest request) {
        return ticketService.assignTicket(id, request.getVendorId());
    }

    // Vendor only
    @PutMapping("/{id}/status")
    public Ticket updateStatus(@PathVariable String id,
                               @RequestBody UpdateStatusRequest request) {
        return ticketService.updateStatus(id, request.getStatus());
    }

    // Buyer only
    @PutMapping("/{id}/close")
    public Ticket close(@PathVariable String id) {
        return ticketService.closeTicket(id);
    }
}
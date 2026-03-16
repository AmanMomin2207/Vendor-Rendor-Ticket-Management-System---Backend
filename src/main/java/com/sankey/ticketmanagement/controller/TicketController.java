package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.*;
import com.sankey.ticketmanagement.exception.ResourceNotFoundException;
import com.sankey.ticketmanagement.model.Priority;
import com.sankey.ticketmanagement.model.Ticket;
import com.sankey.ticketmanagement.model.TicketHistory;
import com.sankey.ticketmanagement.model.TicketStatus;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.repository.TicketRepository;
import com.sankey.ticketmanagement.service.FileStorageService;
import com.sankey.ticketmanagement.service.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import java.io.InputStream;
import java.io.IOException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final FileStorageService fileStorageService;


    public TicketController(TicketService ticketService, TicketRepository ticketRepository, FileStorageService fileStorageService) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.fileStorageService = fileStorageService;
    }

    @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}/assign")
        public ApiResponse<Ticket> assignTicket(
                @PathVariable String id,
                @RequestBody Map<String, String> body) {

                String vendorId = body.get("vendorId");
                System.out.println(">>> Assigning ticket: " + id + " to vendor: " + vendorId);

                if (vendorId == null || vendorId.isBlank()) {
                        return new ApiResponse<>(false, "vendorId is required", null);
                }

                Ticket ticket = ticketService.assignTicket(id, vendorId);
                return new ApiResponse<>(true, "Ticket assigned successfully", ticket);
        }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @GetMapping("/{id}")
    public ApiResponse<Ticket> getTicketById(@PathVariable String id,
                                            Authentication authentication) {
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next()
                        .getAuthority().replace("ROLE_", "");

        return new ApiResponse<>(true, "Ticket fetched", ticketService.getTicketById(id, email, role));
    }
    
    // Buyer only
    @PreAuthorize("hasRole('BUYER')")
        @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ApiResponse<Ticket> create(
                @RequestParam("title") String title,
                @RequestParam("description") String description,
                @RequestParam("priority") String priority,
                @RequestParam(value = "file", required = false) MultipartFile file
        ) throws IOException {

        Ticket ticket = ticketService.createTicket(
                title,
                description,
                Priority.valueOf(priority.toUpperCase()),
                file
        );
        return new ApiResponse<>(true, "Ticket created successfully", ticket);
        }

    // Vendor only
    @PreAuthorize("hasRole('VENDOR')")
    @PutMapping("/{id}/status")
    public ApiResponse<Ticket> updateStatus(@PathVariable String id,
                                            @RequestBody UpdateStatusRequest request) {
        Ticket ticket = ticketService.updateStatus(
                id,
                request.getStatus(),
                request.getResolutionNote()  // 👈 pass it through
        );
        return new ApiResponse<>(true, "Status updated successfully", ticket);
    }

    // Buyer only
    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{id}/close")
    public ApiResponse<Ticket> close(@PathVariable String id) {
        Ticket ticket = ticketService.closeTicket(id);
        return new ApiResponse<>(true, "Ticket closed successfully", ticket);
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @GetMapping
    public ApiResponse<PagedResponse<Ticket>> getTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication) {

        String email = authentication.getName();

        return new ApiResponse<>(
                true,
                "Tickets fetched successfully",
                ticketService.getTickets(
                        page,
                        size,
                        status,
                        priority,
                        search,
                        sortBy,
                        direction,
                        email
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @GetMapping("/{id}/history")
    public ApiResponse<List<TicketHistory>> getTicketHistory(@PathVariable String id) {
        return new ApiResponse<>(true, "History fetched", ticketService.getTicketHistory(id));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportTickets() {

        ByteArrayInputStream csvData = ticketService.exportTicketsToCSV();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=tickets.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(csvData));
    }

    // New: download attachment — returns Base64 data
    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER', 'VENDOR')")
        @GetMapping("/{id}/attachment/download")
        public ResponseEntity<InputStreamResource> downloadAttachment(
                @PathVariable String id) throws IOException {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (ticket.getFileId() == null) {
                return ResponseEntity.notFound().build();
        }

        InputStream inputStream = fileStorageService.downloadFile(ticket.getFileId());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ticket.getAttachmentType()))
                .header("Content-Disposition",
                        "inline; filename=\"" + ticket.getAttachmentName() + "\"")
                .body(new InputStreamResource(inputStream));
        }

}
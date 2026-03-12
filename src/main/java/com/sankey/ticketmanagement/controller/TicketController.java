package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.*;
import com.sankey.ticketmanagement.model.Priority;
import com.sankey.ticketmanagement.model.Ticket;
import com.sankey.ticketmanagement.model.TicketAttachment;
import com.sankey.ticketmanagement.model.TicketHistory;
import com.sankey.ticketmanagement.model.TicketStatus;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.service.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin("*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    //Admin only
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
    @PostMapping("/create")
    public ApiResponse<Ticket> create(@RequestBody CreateTicketRequest request) {
        Ticket ticket = ticketService.createTicket(
                request.getTitle(),
                request.getDescription(),
                request.getPriority()
        );
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

    // ── Upload attachment ──────────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @PostMapping("/{id}/attachments")
    public ApiResponse<Ticket> uploadAttachment(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) throws Exception {

        var data = fileService.processFile(file);

        TicketAttachment attachment = new TicketAttachment(
                data.id(),
                data.fileName(),
                data.fileType(),
                data.fileSize(),
                data.base64Data()
        );

        Ticket ticket = ticketService.addAttachment(id, attachment);
        return new ApiResponse<>(true, "File uploaded successfully", ticket);
    }

    // ── Delete attachment ──────────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public ApiResponse<Ticket> deleteAttachment(
            @PathVariable String id,
            @PathVariable String attachmentId) {

        Ticket ticket = ticketService.removeAttachment(id, attachmentId);
        return new ApiResponse<>(true, "Attachment deleted", ticket);
    }

    // ── Download/view a single attachment ─────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @GetMapping("/{id}/attachments/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable String id,
            @PathVariable String attachmentId) {

        TicketAttachment att = ticketService.getAttachment(id, attachmentId);

        byte[] bytes = Base64.getDecoder().decode(att.getBase64Data());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + att.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(att.getFileType()))
                .body(bytes);
    }
}
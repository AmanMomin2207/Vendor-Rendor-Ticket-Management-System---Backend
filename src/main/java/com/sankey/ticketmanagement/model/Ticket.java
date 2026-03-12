package com.sankey.ticketmanagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;

    private String title;

    private String description;

    private Priority priority;

    private TicketStatus status;

    private String createdBy;   // Buyer ID

    private String assignedTo;  // Vendor ID

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime closedAt;

    private String resolutionNote;

    private String attachmentName;     // original file name

    private String attachmentType;     // MIME type e.g. application/pdf
    
    private String attachmentData;     // Base64 encoded file content
}
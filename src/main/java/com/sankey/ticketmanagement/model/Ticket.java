package com.sankey.ticketmanagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    private String id;
    private String title;
    private String description;
    private Priority priority;
    private TicketStatus status;
    private String createdBy;
    private String assignedTo;
    private String resolutionNote;

    // ✅ GridFS fields — replaces attachmentData (no more Base64 in DB)
    private String fileId;           // GridFS ObjectId reference
    private String attachmentName;   // original filename
    private String attachmentType;   // MIME type
    private Long attachmentSize;     // file size in bytes

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
}
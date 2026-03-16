package com.sankey.ticketmanagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "ticket_history")
public class TicketHistory {

    @Id
    private String id;

    private String ticketId;

    private TicketStatus oldStatus;

    private TicketStatus newStatus;

    private String changedBy;

    private LocalDateTime changedAt;
}
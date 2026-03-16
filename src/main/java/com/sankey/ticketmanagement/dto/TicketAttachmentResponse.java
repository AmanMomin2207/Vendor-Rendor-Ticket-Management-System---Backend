package com.sankey.ticketmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketAttachmentResponse {
    private String ticketId;
    private String attachmentName;
    private String attachmentType;
    private boolean hasAttachment;
}
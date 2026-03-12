package com.sankey.ticketmanagement.dto;

import com.sankey.ticketmanagement.model.Priority;
import lombok.Data;

@Data
public class CreateTicketRequest {
    private String title;
    private String description;
    private Priority priority;
    private String attachmentName; // optional
    private String attachmentType; // optional
    private String attachmentData; // optional — Base64 string
}
package com.sankey.ticketmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketAttachment {
    private String id;          // UUID we generate
    private String fileName;
    private String fileType;
    private long fileSize;
    private String base64Data;  // actual file content stored here
}
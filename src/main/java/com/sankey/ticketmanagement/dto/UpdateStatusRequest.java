package com.sankey.ticketmanagement.dto;

import com.sankey.ticketmanagement.model.TicketStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private TicketStatus status;
}
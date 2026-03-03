package com.sankey.ticketmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse {

    private long total;
    private long open;
    private long inProgress;
    private long resolved;
    private long closed;
}
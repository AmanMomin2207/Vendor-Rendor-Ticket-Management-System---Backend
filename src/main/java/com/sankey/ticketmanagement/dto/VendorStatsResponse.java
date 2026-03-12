package com.sankey.ticketmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VendorStatsResponse {
    private String vendorId;
    private String vendorName;
    private String vendorEmail;
    private long totalAssigned;
    private long inProgress;
    private long resolved;
    private long closed;
    private double resolutionRate;
    private String avgResolutionTime;
}
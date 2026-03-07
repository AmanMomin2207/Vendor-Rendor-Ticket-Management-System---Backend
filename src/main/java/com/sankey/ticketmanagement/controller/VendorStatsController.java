package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.VendorStatsResponse;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.service.VendorStatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vendors")
@CrossOrigin("*")
public class VendorStatsController {

    private final VendorStatsService vendorStatsService;

    public VendorStatsController(VendorStatsService vendorStatsService) {
        this.vendorStatsService = vendorStatsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ApiResponse<List<VendorStatsResponse>> getVendorStats() {
        return new ApiResponse<>(true, "Vendor stats fetched",
                vendorStatsService.getAllVendorStats());
    }
}
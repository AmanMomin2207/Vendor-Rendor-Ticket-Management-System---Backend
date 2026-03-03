package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.DashboardResponse;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/{buyerId}")
    public ApiResponse<DashboardResponse> buyerDashboard(@PathVariable String buyerId) {
        return new ApiResponse<>(true, "Buyer Dashboard",
                dashboardService.getBuyerDashboard(buyerId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<DashboardResponse> adminDashboard() {
        return new ApiResponse<>(true, "Admin Dashboard",
                dashboardService.getAdminDashboard());
    }

    @PreAuthorize("hasRole('VENDOR')")
    @GetMapping("/vendor/{vendorId}")
    public ApiResponse<DashboardResponse> vendorDashboard(@PathVariable String vendorId) {
        return new ApiResponse<>(true, "Vendor Dashboard",
                dashboardService.getVendorDashboard(vendorId));
    }
}
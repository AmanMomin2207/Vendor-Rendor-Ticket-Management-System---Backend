package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.DashboardResponse;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer")
    public ApiResponse<DashboardResponse> buyerDashboard(Authentication authentication) {
        
        String email = authentication.getName();
        
        return new ApiResponse<>(true, "Buyer Dashboard",
                dashboardService.getBuyerDashboardByEmail(email));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<DashboardResponse> adminDashboard() {
        return new ApiResponse<>(true, "Admin Dashboard",
                dashboardService.getAdminDashboard());
    }

    @PreAuthorize("hasRole('VENDOR')")
    @GetMapping("/vendor")
    public ApiResponse<DashboardResponse> vendorDashboard(Authentication authentication) {
        
        String email = authentication.getName();

        return new ApiResponse<>(true, "Vendor Dashboard",
                dashboardService.getVendorDashboardByEmail(email));
    }
}
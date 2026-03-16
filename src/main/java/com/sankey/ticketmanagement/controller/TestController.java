package com.sankey.ticketmanagement.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin access granted";
    }

    @GetMapping("/vendor/test")
    public String vendorTest() {
        return "Vendor access granted";
    }

    @GetMapping("/buyer/test")
    public String buyerTest() {
        return "Buyer access granted";
    }
}
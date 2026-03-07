package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.UpdateProfileRequest;
import com.sankey.ticketmanagement.model.User;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin("*")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @GetMapping
    public ApiResponse<User> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return new ApiResponse<>(true, "Profile fetched", userService.getMyProfile(email));
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @PutMapping
    public ApiResponse<User> updateProfile(Authentication authentication,
                                            @RequestBody UpdateProfileRequest request) {
        String email = authentication.getName();
        return new ApiResponse<>(true, "Profile updated", userService.updateMyProfile(email, request));
    }
}
package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.model.User;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        return new ApiResponse<>(true, "Users fetched", userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable String id) {
        return new ApiResponse<>(true, "User fetched", userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/toggle")
    public ApiResponse<User> toggleUserStatus(@PathVariable String id) {
        return new ApiResponse<>(true, "Status updated", userService.toggleUserStatus(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ApiResponse<>(true, "User deleted", null);
    }
}
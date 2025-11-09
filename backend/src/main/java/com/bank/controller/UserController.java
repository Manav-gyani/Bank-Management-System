package com.bank.controller;

import com.bank.dto.request.UpdatePasswordRequest;
import com.bank.dto.request.UpdateProfileRequest;
import com.bank.dto.response.ApiResponse;
import com.bank.dto.response.ProfileResponse;
import com.bank.dto.response.UserResponse;
import com.bank.model.User;
import com.bank.security.UserPrincipal;
import com.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current logged-in user details
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    /**
     * Get current user profile with customer data
     */
    @GetMapping("/me/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getCurrentProfile() {
        return ResponseEntity.ok(userService.getCurrentProfile());
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    /**
     * Get all users (Admin/Employee only)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    /**
     * Update complete profile (User + Customer data)
     * This endpoint updates both User and Customer collections to ensure data consistency
     */
    @PutMapping("/{id}/profile")
    @PreAuthorize("@userSecurity.isOwner(#id)")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    /**
     * Change password
     */
    @PutMapping("/{id}/change-password")
    @PreAuthorize("@userSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse> changePassword(
            @PathVariable String id,
            @Valid @RequestBody UpdatePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
    }

    /**
     * Delete user (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
    }

    /**
     * Enable user account (Admin only)
     */
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> enableUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.enableUser(id));
    }

    /**
     * Disable user account (Admin only)
     */
    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> disableUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.disableUser(id));
    }
    
    /**
     * Fix duplicate customer issue - finds and deletes empty duplicate customers
     */
    @PostMapping("/fix-customer-duplicates")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> fixCustomerDuplicates() {
        return ResponseEntity.ok(userService.fixCustomerDuplicates());
    }
}


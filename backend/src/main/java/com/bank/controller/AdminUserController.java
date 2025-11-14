package com.bank.controller;
import com.bank.dto.UserDetailsDto;
import com.bank.model.User;
import com.bank.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
// @PreAuthorize("hasRole('ADMIN')") // Temporarily disabled for testing
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        return ResponseEntity.ok(adminUserService.getAllUsers(page, size, sortBy));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminUserService.searchUsers(query, page, size));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsDto> getUserDetails(@PathVariable String userId) {
        return ResponseEntity.ok(adminUserService.getUserDetails(userId));
    }

    @PutMapping("/{userId}/suspend")
    public ResponseEntity<String> suspendUser(@PathVariable String userId) {
        adminUserService.suspendUser(userId);
        return ResponseEntity.ok("User suspended successfully");
    }

    @PutMapping("/{userId}/activate")
    public ResponseEntity<String> activateUser(@PathVariable String userId) {
        adminUserService.activateUser(userId);
        return ResponseEntity.ok("User activated successfully");
    }
}

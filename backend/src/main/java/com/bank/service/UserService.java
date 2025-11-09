package com.bank.service;

import com.bank.dto.request.UpdatePasswordRequest;
import com.bank.dto.request.UpdateProfileRequest;
import com.bank.dto.response.ApiResponse;
import com.bank.dto.response.ProfileResponse;
import com.bank.dto.response.UserResponse;
import com.bank.exception.BadRequestException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Customer;
import com.bank.model.User;
import com.bank.repository.CustomerRepository;
import com.bank.repository.UserRepository;
import com.bank.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        return new UserResponse(user);
    }

    /**
     * Get current user profile with customer data
     */
    public ProfileResponse getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        // Find customer, handling duplicates by using the oldest one
        Customer customer = findCustomerByEmailSafe(user.getEmail());
        
        // Create customer if it doesn't exist
        if (customer == null && user.getRoles().contains(User.Role.CUSTOMER)) {
            System.out.println("⚠️ No customer profile found for user: " + user.getEmail() + ", creating one...");
            customer = new Customer();
            customer.setEmail(user.getEmail());
            customer.setFirstName("");
            customer.setLastName("");
            customer.setPhone("");
            customer.setAddress("");
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customer = customerRepository.save(customer);
            System.out.println("✅ Customer profile created with ID: " + customer.getId());
        }

        return new ProfileResponse(user, customer);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new UserResponse(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return new UserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(String id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (userDetails.getEmail() != null) {
            // Check if email already exists for another user
            userRepository.findByEmail(userDetails.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new BadRequestException("Email already in use");
                        }
                    });
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getUsername() != null) {
            // Check if username already exists for another user
            userRepository.findByUsername(userDetails.getUsername())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new BadRequestException("Username already taken");
                        }
                    });
            user.setUsername(userDetails.getUsername());
        }

        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser);
    }

    /**
     * Update profile - Updates both User and Customer collections
     * This ensures data consistency across all collections
     */
    public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
        System.out.println("📝 Updating profile for user ID: " + userId);
        System.out.println("📝 Request data: " + request);
        
        // Update User collection
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Store old email to find customer before updating
        String oldEmail = user.getEmail();
        boolean userUpdated = false;
        boolean emailChanged = false;

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if email already exists for another user
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(userId)) {
                            throw new BadRequestException("Email already in use");
                        }
                    });
            System.out.println("✏️ Updating email from " + user.getEmail() + " to " + request.getEmail());
            user.setEmail(request.getEmail());
            emailChanged = true;
            userUpdated = true;
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            // Check if username already exists for another user
            userRepository.findByUsername(request.getUsername())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(userId)) {
                            throw new BadRequestException("Username already taken");
                        }
                    });
            System.out.println("✏️ Updating username from " + user.getUsername() + " to " + request.getUsername());
            user.setUsername(request.getUsername());
            userUpdated = true;
        }

        // Find customer by OLD email BEFORE updating user
        Customer customer = findCustomerByEmailSafe(oldEmail);
        
        System.out.println("🔍 Searching for customer with email: " + oldEmail);
        if (customer != null) {
            System.out.println("✅ Found existing customer: " + customer.getId());
        }

        // Now save user updates
        if (userUpdated) {
            user = userRepository.save(user);
            System.out.println("✅ User collection updated");
        }

        // Create customer if doesn't exist
        if (customer == null && user.getRoles().contains(User.Role.CUSTOMER)) {
            System.out.println("⚠️ No customer found, creating new customer profile...");
            customer = new Customer();
            customer.setEmail(user.getEmail());
            customer.setFirstName(request.getFirstName() != null ? request.getFirstName() : "");
            customer.setLastName(request.getLastName() != null ? request.getLastName() : "");
            customer.setPhone(request.getPhone() != null ? request.getPhone() : "");
            customer.setAddress(request.getAddress() != null ? request.getAddress() : "");
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
            System.out.println("✅ New customer profile created");
        } else if (customer != null) {
            System.out.println("👤 Found customer: " + customer.getId());
            boolean customerUpdated = false;

            if (request.getFirstName() != null) {
                System.out.println("✏️ Updating firstName to: " + request.getFirstName());
                customer.setFirstName(request.getFirstName());
                customerUpdated = true;
            }

            if (request.getLastName() != null) {
                System.out.println("✏️ Updating lastName to: " + request.getLastName());
                customer.setLastName(request.getLastName());
                customerUpdated = true;
            }

            if (request.getPhone() != null) {
                System.out.println("✏️ Updating phone to: " + request.getPhone());
                customer.setPhone(request.getPhone());
                customerUpdated = true;
            }

            if (request.getAddress() != null) {
                System.out.println("✏️ Updating address to: " + request.getAddress());
                customer.setAddress(request.getAddress());
                customerUpdated = true;
            }

            // Update email in customer collection if it changed
            if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
                System.out.println("✏️ Updating customer email to: " + request.getEmail());
                customer.setEmail(request.getEmail());
                customerUpdated = true;
            }

            if (customerUpdated) {
                customer.setUpdatedAt(LocalDateTime.now());
                customerRepository.save(customer);
                System.out.println("✅ Customer collection updated");
            }
        } else {
            System.out.println("⚠️ No customer found for email: " + user.getEmail());
        }

        System.out.println("✅ Profile update complete");
        return new UserResponse(user);
    }

    public void changePassword(String userId, UpdatePasswordRequest request) {
        System.out.println("🔐 Changing password for user ID: " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            System.out.println("❌ Old password verification failed");
            throw new BadRequestException("Old password is incorrect");
        }

        System.out.println("✅ Old password verified");
        
        // Set new password
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
        
        System.out.println("✅ Password updated in database");
        System.out.println("✅ User can now login with the new password");
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    public UserResponse enableUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setEnabled(true);
        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser);
    }

    public UserResponse disableUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setEnabled(false);
        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser);
    }

    public boolean checkUsernameAvailability(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean checkEmailAvailability(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    /**
     * Safely find customer by email, handling duplicates by returning the oldest
     */
    private Customer findCustomerByEmailSafe(String email) {
        // Use findAllByEmail to avoid non-unique result errors
        List<Customer> customers = customerRepository.findAllByEmail(email);
        
        if (customers.isEmpty()) {
            return null;
        }
        
        if (customers.size() > 1) {
            System.out.println("⚠️ Multiple customers found with email: " + email + ", using oldest one");
        }
        
        // Sort by createdAt and return the oldest
        customers.sort((c1, c2) -> {
            if (c1.getCreatedAt() == null) return 1;
            if (c2.getCreatedAt() == null) return -1;
            return c1.getCreatedAt().compareTo(c2.getCreatedAt());
        });
        
        Customer oldest = customers.get(0);
        System.out.println("✅ Using customer: " + oldest.getId());
        return oldest;
    }
    
    /**
     * Fix duplicate customer records by keeping the oldest one
     */
    public ApiResponse fixCustomerDuplicates() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        String email = user.getEmail();
        System.out.println("🔧 Fixing customer duplicates for email: " + email);
        
        // Find all customers with this email
        List<Customer> customers = customerRepository.findAll().stream()
                .filter(c -> email.equals(c.getEmail()))
                .collect(Collectors.toList());
        
        System.out.println("🔍 Found " + customers.size() + " customer(s) with email: " + email);
        
        if (customers.size() <= 1) {
            return new ApiResponse(true, "No duplicate customers found");
        }
        
        // Sort by createdAt to find the oldest
        customers.sort((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));
        
        Customer oldestCustomer = customers.get(0);
        System.out.println("✅ Keeping oldest customer: " + oldestCustomer.getId() + " (created: " + oldestCustomer.getCreatedAt() + ")");
        
        // Delete all duplicates
        for (int i = 1; i < customers.size(); i++) {
            Customer duplicate = customers.get(i);
            System.out.println("❌ Deleting duplicate customer: " + duplicate.getId() + " (created: " + duplicate.getCreatedAt() + ")");
            customerRepository.deleteById(duplicate.getId());
        }
        
        System.out.println("✅ Duplicate customers removed successfully");
        return new ApiResponse(true, "Removed " + (customers.size() - 1) + " duplicate customer(s). Kept customer ID: " + oldestCustomer.getId());
    }
}
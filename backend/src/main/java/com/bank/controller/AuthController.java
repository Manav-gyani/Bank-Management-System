package com.bank.controller;

import com.bank.dto.request.RegisterRequest;
import com.bank.dto.response.ApiResponse;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {

        // Validation
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Username is already taken!"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email is already in use!"));
        }

        try {
            // ========================================
            // STEP 1: Create Customer Profile FIRST
            // ========================================
            Customer customer = new Customer();
            customer.setFirstName(request.getFirstName());
            customer.setLastName(request.getLastName());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());
            customer.setAddress(request.getAddress());
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());

            Customer savedCustomer = customerRepository.save(customer);
            System.out.println("✅ Customer created with ID: " + savedCustomer.getId());

            // ========================================
            // STEP 2: Create User Account for Login
            // ========================================
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEnabled(true);

            Set<User.Role> roles = new HashSet<>();
            roles.add(User.Role.CUSTOMER);
            user.setRoles(roles);

            User savedUser = userRepository.save(user);
            System.out.println("✅ User created with ID: " + savedUser.getId());

            // ========================================
            // STEP 3: Create Default Savings Account (Linked to Customer)
            // ========================================
            Account defaultAccount = new Account();
            defaultAccount.setAccountNumber(generateAccountNumber());
            defaultAccount.setCustomerId(savedCustomer.getId()); // ← LINKING HERE!
            defaultAccount.setAccountType(Account.AccountType.SAVINGS);
            defaultAccount.setBalance(BigDecimal.ZERO);
            defaultAccount.setCurrency("INR");
            defaultAccount.setStatus(Account.AccountStatus.ACTIVE);
            defaultAccount.setCreatedAt(LocalDateTime.now());
            defaultAccount.setUpdatedAt(LocalDateTime.now());

            Account savedAccount = accountRepository.save(defaultAccount);
            System.out.println("✅ Account created: " + savedAccount.getAccountNumber() +
                    " for Customer ID: " + savedCustomer.getId());

            // Success response
            return ResponseEntity.ok(new ApiResponse(true,
                    "User registered successfully! Your account number is: " +
                            savedAccount.getAccountNumber()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error during registration: " + e.getMessage()));
        }
    }

    private String generateAccountNumber() {
        // Generate unique 12-digit account number
        return String.format("%012d", System.currentTimeMillis() % 1000000000000L);
    }
}

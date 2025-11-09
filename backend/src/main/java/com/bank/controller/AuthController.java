package com.bank.controller;

import com.bank.dto.request.LoginRequest;
import com.bank.dto.request.RegisterRequest;
import com.bank.dto.response.ApiResponse;
import com.bank.dto.response.AuthResponse;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.UserRepository;
import com.bank.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create basic response
            AuthResponse response = new AuthResponse(jwt, user.getUsername(), user.getEmail());

            // Try to find customer by email (only for CUSTOMER role users)
            if (user.getRoles().contains(User.Role.CUSTOMER)) {
                Customer customer = customerRepository.findByEmail(user.getEmail())
                        .orElse(null);

                if (customer != null) {
                    response.setCustomerId(customer.getId());
                    System.out.println("✅ Login successful - Customer ID: " + customer.getId());
                } else {
                    // Create customer profile if it doesn't exist for CUSTOMER role users
                    System.out.println("⚠️ No customer profile found, creating one...");
                    Customer newCustomer = new Customer();
                    newCustomer.setEmail(user.getEmail());
                    newCustomer.setFirstName(user.getUsername()); // Default to username
                    newCustomer.setLastName("");
                    newCustomer.setCreatedAt(LocalDateTime.now());
                    newCustomer.setUpdatedAt(LocalDateTime.now());
                    
                    Customer savedCustomer = customerRepository.save(newCustomer);
                    response.setCustomerId(savedCustomer.getId());
                    System.out.println("✅ Customer profile created - Customer ID: " + savedCustomer.getId());
                }
            } else {
                System.out.println("ℹ️ User is not a CUSTOMER role, no customerId required");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Username is already taken!"));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email Address already in use!"));
        }

        try {
            // STEP 1: Create Customer Profile FIRST
            Customer customer = new Customer();
            customer.setFirstName(registerRequest.getFirstName());
            customer.setLastName(registerRequest.getLastName());
            customer.setEmail(registerRequest.getEmail());
            customer.setPhone(registerRequest.getPhone());
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());

            Customer savedCustomer = customerRepository.save(customer);
            System.out.println("✅ Customer created with ID: " + savedCustomer.getId());

            // STEP 2: Create User Account for Login
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setEnabled(true);

            Set<User.Role> roles = new HashSet<>();
            roles.add(User.Role.CUSTOMER);
            user.setRoles(roles);

            userRepository.save(user);
            System.out.println("✅ User created: " + user.getUsername());

            // STEP 3: Create Default Savings Account
            Account defaultAccount = new Account();
            defaultAccount.setAccountNumber(generateAccountNumber());
            defaultAccount.setCustomerId(savedCustomer.getId());
            defaultAccount.setAccountType(Account.AccountType.SAVINGS);
            defaultAccount.setBalance(BigDecimal.ZERO);
            defaultAccount.setCurrency("INR");
            defaultAccount.setStatus(Account.AccountStatus.ACTIVE);
            defaultAccount.setCreatedAt(LocalDateTime.now());
            defaultAccount.setUpdatedAt(LocalDateTime.now());

            Account savedAccount = accountRepository.save(defaultAccount);
            System.out.println("✅ Default account created: " + savedAccount.getAccountNumber());

            // Return enhanced response with customer details
            AuthResponse authResponse = new AuthResponse();
            authResponse.setUsername(user.getUsername());
            authResponse.setEmail(user.getEmail());
            authResponse.setCustomerId(savedCustomer.getId());
            authResponse.setTokenType("Registration Success");
            
            System.out.println("✅ Registration complete - Customer ID: " + savedCustomer.getId() + ", Account: " + savedAccount.getAccountNumber());
            
            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            System.err.println("❌ Registration failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return ResponseEntity.ok(new ApiResponse(isAvailable,
                isAvailable ? "Username is available" : "Username is already taken"));
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return ResponseEntity.ok(new ApiResponse(isAvailable,
                isAvailable ? "Email is available" : "Email is already in use"));
    }

    private String generateAccountNumber() {
        return String.format("%012d", System.currentTimeMillis() % 1000000000000L);
    }
}

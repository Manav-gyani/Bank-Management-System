package com.bank.service;

import com.bank.dto.request.LoginRequest;
import com.bank.dto.request.RegisterRequest;
import com.bank.dto.response.AuthResponse;
import com.bank.exception.BadRequestException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.UserRepository;
import com.bank.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

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

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Check if user exists first
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BadRequestException("Invalid username or password"));

            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            AuthResponse response = new AuthResponse(jwt, user.getUsername(), user.getEmail());
            response.setUserId(user.getId()); // Set user ID for frontend

            // Try to find customer by email (only for CUSTOMER role users)
            if (user.getRoles().contains(User.Role.CUSTOMER)) {
                // Find all customers with this email and use the oldest one (first created)
                Customer customer = findCustomerByEmailSafe(user.getEmail());

                if (customer != null) {
                    response.setCustomerId(customer.getId());
                    System.out.println("✅ Login successful - Customer ID: " + customer.getId());
                } else {
                    // Create customer profile if it doesn't exist for CUSTOMER role users
                    System.out.println("⚠️ No customer profile found, creating one...");
                    Customer newCustomer = new Customer();
                    newCustomer.setEmail(user.getEmail());
                    newCustomer.setFirstName(user.getUsername());
                    newCustomer.setLastName("");
                    newCustomer.setCreatedAt(LocalDateTime.now());
                    newCustomer.setUpdatedAt(LocalDateTime.now());
                    
                    Customer savedCustomer = customerRepository.save(newCustomer);
                    response.setCustomerId(savedCustomer.getId());
                    System.out.println("✅ Customer profile created - Customer ID: " + savedCustomer.getId());
                }
            }
            
            return response;
        } catch (org.springframework.security.core.AuthenticationException e) {
            // For security, use generic message instead of exposing specific error
            throw new BadRequestException("Invalid username or password");
        } catch (BadRequestException e) {
            // Re-throw BadRequestException as-is
            throw e;
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            throw new BadRequestException("Login failed: " + e.getMessage());
        }
    }

    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        // Create Customer
        Customer customer = new Customer();
        customer.setFirstName(registerRequest.getFirstName());
        customer.setLastName(registerRequest.getLastName());
        customer.setEmail(registerRequest.getEmail());
        customer.setPhone(registerRequest.getPhone());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        Customer savedCustomer = customerRepository.save(customer);

        // Create User
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);

        Set<User.Role> roles = new HashSet<>();
        roles.add(User.Role.CUSTOMER);
        user.setRoles(roles);
        userRepository.save(user);

        // Create Default Account
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setCustomerId(savedCustomer.getId());
        account.setAccountType(Account.AccountType.SAVINGS);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("INR");
        account.setStatus(Account.AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        return "User registered successfully!";
    }

    private String generateAccountNumber() {
        return String.format("%012d", System.currentTimeMillis() % 1000000000000L);
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
}
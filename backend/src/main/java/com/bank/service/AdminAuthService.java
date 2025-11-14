package com.bank.service;

import com.bank.dto.request.AdminLoginRequest;
import com.bank.dto.response.AdminLoginResponse;
import com.bank.model.Admin;
import com.bank.repository.AdminRepository;
import com.bank.security.JwtTokenProvider;
import com.bank.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

        // Create Authentication object for token generation
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserPrincipal userPrincipal = new UserPrincipal(admin.getId(), admin.getUsername(), admin.getEmail(), admin.getPassword(), authorities, true);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        String token = jwtTokenProvider.generateToken(authentication);

        AdminLoginResponse response = new AdminLoginResponse();
        response.setToken(token);
        response.setUsername(admin.getUsername());
        response.setEmail(admin.getEmail());

        return response;
    }
}

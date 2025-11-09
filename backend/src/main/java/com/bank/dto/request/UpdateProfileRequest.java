package com.bank.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    // User fields
    private String username;
    
    @Email(message = "Email should be valid")
    private String email;
    
    // Customer fields
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}

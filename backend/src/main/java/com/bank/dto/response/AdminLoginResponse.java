package com.bank.dto.response;

import lombok.Data;

@Data
public class AdminLoginResponse {
    private String token;
    private String username;
    private String email;
}
package com.bank.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "admins")
public class Admin {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
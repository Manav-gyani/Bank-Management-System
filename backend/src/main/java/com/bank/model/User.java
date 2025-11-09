package com.bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "users")
@Component
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private Set<Role> roles=new HashSet<>();
    private boolean enabled;

    public enum Role {
        CUSTOMER, EMPLOYEE, MANAGER, ADMIN
    }
}
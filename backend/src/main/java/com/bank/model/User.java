package com.bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private Set<Role> roles;
    private boolean enabled;

    public enum Role {
        CUSTOMER, EMPLOYEE, MANAGER, ADMIN
    }
}
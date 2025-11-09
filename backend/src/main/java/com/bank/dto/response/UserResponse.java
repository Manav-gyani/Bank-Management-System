package com.bank.dto.response;

import com.bank.model.User;
import lombok.Data;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Set<String> roles;
    private Boolean enabled;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
//        this.enabled = user.getEnabled();
    }
}
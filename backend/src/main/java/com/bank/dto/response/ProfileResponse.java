package com.bank.dto.response;

import com.bank.model.Customer;
import com.bank.model.User;
import lombok.Data;

@Data
public class ProfileResponse {
    // User data
    private String id;
    private String username;
    private String email;
    
    // Customer data
    private String customerId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    
    public ProfileResponse(User user, Customer customer) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        
        if (customer != null) {
            this.customerId = customer.getId();
            this.firstName = customer.getFirstName();
            this.lastName = customer.getLastName();
            this.phone = customer.getPhone();
            this.address = customer.getAddress();
        }
    }
}

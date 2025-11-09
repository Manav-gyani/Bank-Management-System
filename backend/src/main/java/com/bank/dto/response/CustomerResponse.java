package com.bank.dto.response;

import com.bank.model.Customer;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String aadharNumber;
    private String panNumber;
    private LocalDateTime createdAt;

    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.email = customer.getEmail();
        this.phone = customer.getPhone();
        this.address = customer.getAddress();
        this.aadharNumber = customer.getAadharNumber();
        this.panNumber = customer.getPanNumber();
        this.createdAt = customer.getCreatedAt();
    }
}
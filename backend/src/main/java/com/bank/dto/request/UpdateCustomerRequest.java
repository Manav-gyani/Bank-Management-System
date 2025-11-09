package com.bank.dto.request;

import lombok.Data;

@Data
public class UpdateCustomerRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String aadharNumber;
    private String panNumber;
}
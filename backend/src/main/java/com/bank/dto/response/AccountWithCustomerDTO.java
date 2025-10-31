package com.bank.dto.response;

import com.bank.model.Account;
import com.bank.model.Customer;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountWithCustomerDTO {
    // Account fields
    private String accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String status;
    private LocalDateTime createdAt;

    // Customer fields
    private String customerId;
    private String customerName;
    private String email;
    private String phone;
    private String address;

    public AccountWithCustomerDTO(Account account, Customer customer) {
        // Account details
        this.accountId = account.getId();
        this.accountNumber = account.getAccountNumber();
        this.accountType = account.getAccountType().name();
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
        this.status = account.getStatus().name();
        this.createdAt = account.getCreatedAt();

        // Customer details
        this.customerId = customer.getId();
        this.customerName = customer.getFirstName() + " " + customer.getLastName();
        this.email = customer.getEmail();
        this.phone = customer.getPhone();
        this.address = customer.getAddress();
    }
}
package com.bank.dto.response;

import com.bank.model.Account;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private String id;
    private String accountNumber;
    private String customerId;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccountResponse(Account account) {
        this.id = account.getId();
        this.accountNumber = account.getAccountNumber();
        this.customerId = account.getCustomerId();
        this.accountType = account.getAccountType().name();
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
        this.status = account.getStatus().name();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }
}
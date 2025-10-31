package com.bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    private String accountNumber;
    @DBRef
    private String customerId;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum AccountType {
        SAVINGS, CURRENT, FIXED_DEPOSIT, RECURRING_DEPOSIT
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE, SUSPENDED, CLOSED
    }
}

package com.bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "transactions")
@Component
public class Transaction {
    @Id
    private String id;
    private String transactionId;
    private String accountId;
    private TransactionType type;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceNumber;
    private TransactionStatus status;
    private LocalDateTime timestamp;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, INTEREST
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, REVERSED
    }
    public Transaction() {
        this.transactionId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.referenceNumber = "REF" + System.currentTimeMillis();
    }
}

package com.bank.dto.response;

import com.bank.model.Transaction;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private String id;
    private String transactionId;
    private String accountId;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceNumber;
    private String status;
    private LocalDateTime timestamp;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.transactionId = transaction.getTransactionId();
        this.accountId = transaction.getAccountId();
        this.type = transaction.getType().name();
        this.amount = transaction.getAmount();
        this.balanceAfter = transaction.getBalanceAfter();
        this.description = transaction.getDescription();
        this.referenceNumber = transaction.getReferenceNumber();
        this.status = transaction.getStatus().name();
        this.timestamp = transaction.getTimestamp();
    }
}
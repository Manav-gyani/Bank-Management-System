package com.bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "cards")
public class Card {
    @Id
    private String id;
    private String cardNumber;
    private String customerId;
    private String accountId;
    private CardType cardType;
    private String cardHolderName;
    private LocalDate expiryDate;
    private String cvv;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private CardStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum CardType {
        DEBIT_CARD, CREDIT_CARD, PREPAID_CARD
    }

    public enum CardStatus {
        ACTIVE, BLOCKED, EXPIRED, CANCELLED
    }
}
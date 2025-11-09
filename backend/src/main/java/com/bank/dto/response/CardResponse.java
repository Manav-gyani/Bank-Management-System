package com.bank.dto.response;

import com.bank.model.Card;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponse {
    private String id;
    private String cardNumber;
    private String customerId;
    private String accountId;
    private String cardType;
    private String cardHolderName;
    private LocalDate expiryDate;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private String status;

    public CardResponse(Card card) {
        this.id = card.getId();
        this.cardNumber = maskCardNumber(card.getCardNumber());
        this.customerId = card.getCustomerId();
        this.accountId = card.getAccountId();
        this.cardType = card.getCardType().name();
        this.cardHolderName = card.getCardHolderName();
        this.expiryDate = card.getExpiryDate();
        this.creditLimit = card.getCreditLimit();
        this.availableLimit = card.getAvailableLimit();
        this.status = card.getStatus().name();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
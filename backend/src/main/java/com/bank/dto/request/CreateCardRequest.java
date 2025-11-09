package com.bank.dto.request;

import com.bank.model.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateCardRequest {
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Card type is required")
    private Card.CardType cardType;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    private BigDecimal creditLimit;
}
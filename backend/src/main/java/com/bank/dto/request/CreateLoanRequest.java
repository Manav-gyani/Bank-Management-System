package com.bank.dto.request;

import com.bank.model.Loan;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateLoanRequest {
    @NotNull(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Loan type is required")
    private Loan.LoanType loanType;

    @NotNull(message = "Principal amount is required")
    @Positive(message = "Principal amount must be positive")
    private BigDecimal principalAmount;

    @NotNull(message = "Interest rate is required")
    @Positive(message = "Interest rate must be positive")
    private BigDecimal interestRate;

    @NotNull(message = "Tenure is required")
    @Positive(message = "Tenure must be positive")
    private Integer tenureMonths;
}

package com.bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "loans")
public class Loan {
    @Id
    private String id;
    private String loanNumber;
    private String customerId;
    private String accountId;
    private LoanType loanType;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal monthlyEmi;
    private BigDecimal outstandingAmount;
    private LoanStatus status;
    private LocalDateTime disbursementDate;
    private LocalDateTime nextDueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum LoanType {
        HOME_LOAN, PERSONAL_LOAN, CAR_LOAN, EDUCATION_LOAN, BUSINESS_LOAN
    }

    public enum LoanStatus {
        PENDING, APPROVED, DISBURSED, ACTIVE, CLOSED, REJECTED
    }
}

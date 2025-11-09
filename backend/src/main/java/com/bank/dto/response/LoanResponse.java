package com.bank.dto.response;

import com.bank.model.Loan;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanResponse {
    private String id;
    private String loanNumber;
    private String customerId;
    private String accountId;
    private String loanType;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal monthlyEmi;
    private BigDecimal outstandingAmount;
    private String status;
    private LocalDateTime disbursementDate;
    private LocalDateTime createdAt;

    public LoanResponse(Loan loan) {
        this.id = loan.getId();
        this.loanNumber = loan.getLoanNumber();
        this.customerId = loan.getCustomerId();
        this.accountId = loan.getAccountId();
        this.loanType = loan.getLoanType().name();
        this.principalAmount = loan.getPrincipalAmount();
        this.interestRate = loan.getInterestRate();
        this.tenureMonths = loan.getTenureMonths();
        this.monthlyEmi = loan.getMonthlyEmi();
        this.outstandingAmount = loan.getOutstandingAmount();
        this.status = loan.getStatus().name();
        this.disbursementDate = loan.getDisbursementDate();
        this.createdAt = loan.getCreatedAt();
    }
}
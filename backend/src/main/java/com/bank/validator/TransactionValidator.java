package com.bank.validator;

import com.bank.exception.BadRequestException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.model.Account;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TransactionValidator {

    public void validateDeposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Deposit amount must be greater than zero");
        }

        if (amount.compareTo(BigDecimal.valueOf(1000000)) > 0) {
            throw new BadRequestException("Deposit amount exceeds maximum limit");
        }
    }

    public void validateWithdrawal(BigDecimal amount, Account account) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Withdrawal amount must be greater than zero");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: ₹" + account.getBalance() +
                            ", Requested: ₹" + amount
            );
        }

        if (amount.compareTo(BigDecimal.valueOf(50000)) > 0) {
            throw new BadRequestException("Withdrawal amount exceeds daily limit");
        }
    }

    public void validateTransfer(String fromAccount, String toAccount, BigDecimal amount) {
        if (fromAccount == null || fromAccount.trim().isEmpty()) {
            throw new BadRequestException("Source account is required");
        }

        if (toAccount == null || toAccount.trim().isEmpty()) {
            throw new BadRequestException("Destination account is required");
        }

        if (fromAccount.equals(toAccount)) {
            throw new BadRequestException("Cannot transfer to the same account");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be greater than zero");
        }

        if (amount.compareTo(BigDecimal.valueOf(100000)) > 0) {
            throw new BadRequestException("Transfer amount exceeds maximum limit");
        }
    }

    public void validateAccountStatus(Account account) {
        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not active. Status: " + account.getStatus());
        }
    }
}
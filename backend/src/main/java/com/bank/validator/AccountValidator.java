package com.bank.validator;

import com.bank.exception.BadRequestException;
import com.bank.model.Account;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class AccountValidator {

    public void validateAccountCreation(Account account) {
        if (account.getAccountType() == null) {
            throw new BadRequestException("Account type is required");
        }

        if (account.getCustomerId() == null || account.getCustomerId().trim().isEmpty()) {
            throw new BadRequestException("Customer ID is required");
        }
    }

    public void validateBalance(BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Balance cannot be negative");
        }
    }

    public void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new BadRequestException("Account number is required");
        }

        if (!accountNumber.matches("\\d+")) {
            throw new BadRequestException("Account number must contain only digits");
        }
    }

    public void validateAccountStatus(Account.AccountStatus status) {
        if (status == null) {
            throw new BadRequestException("Account status is required");
        }
    }
}
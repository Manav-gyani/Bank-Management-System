package com.bank.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();

    public String generateAccountNumber() {
        // Format: BANK_CODE(4) + BRANCH_CODE(4) + RANDOM(4)
        String bankCode = "1000";
        String branchCode = String.format("%04d", random.nextInt(10000));
        String randomPart = String.format("%04d", random.nextInt(10000));

        return bankCode + branchCode + randomPart;
    }

    public String generateAccountNumberWithTimestamp() {
        // Format: Timestamp based 12-digit number
        return String.format("%012d", System.currentTimeMillis() % 1000000000000L);
    }

    public String generateCustomAccountNumber(String prefix, int length) {
        StringBuilder accountNumber = new StringBuilder(prefix);
        int remainingLength = length - prefix.length();

        for (int i = 0; i < remainingLength; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }

    public String generateTransactionId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.format("%04d", random.nextInt(10000));
        return "TXN" + timestamp + randomSuffix;
    }

    public String generateLoanNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.format("%06d", random.nextInt(1000000));
        return "LN" + timestamp + randomSuffix;
    }

    public String generateCardNumber() {
        // Generate 16-digit card number starting with 4 (Visa)
        StringBuilder cardNumber = new StringBuilder("4");
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    public String generateCVV() {
        return String.format("%03d", random.nextInt(1000));
    }

    public String generateReferenceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.format("%06d", random.nextInt(1000000));
        return "REF" + timestamp + randomSuffix;
    }
}
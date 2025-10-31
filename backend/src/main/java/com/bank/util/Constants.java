package com.bank.util;

import java.math.BigDecimal;

public class Constants {

    // Account Related
    public static final String DEFAULT_CURRENCY = "INR";
    public static final BigDecimal MINIMUM_BALANCE_SAVINGS = BigDecimal.valueOf(1000);
    public static final BigDecimal MINIMUM_BALANCE_CURRENT = BigDecimal.valueOf(5000);
    public static final BigDecimal DAILY_WITHDRAWAL_LIMIT = BigDecimal.valueOf(50000);
    public static final BigDecimal DAILY_TRANSFER_LIMIT = BigDecimal.valueOf(100000);
    public static final BigDecimal MAXIMUM_DEPOSIT_LIMIT = BigDecimal.valueOf(1000000);

    // Transaction Related
    public static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    public static final String TRANSACTION_TYPE_WITHDRAWAL = "WITHDRAWAL";
    public static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";
    public static final String TRANSACTION_TYPE_PAYMENT = "PAYMENT";

    // Interest Rates
    public static final BigDecimal SAVINGS_INTEREST_RATE = BigDecimal.valueOf(4.0); // 4% per annum
    public static final BigDecimal FD_INTEREST_RATE = BigDecimal.valueOf(6.5); // 6.5% per annum
    public static final BigDecimal LOAN_INTEREST_RATE_HOME = BigDecimal.valueOf(8.5); // 8.5% per annum
    public static final BigDecimal LOAN_INTEREST_RATE_PERSONAL = BigDecimal.valueOf(12.0); // 12% per annum
    public static final BigDecimal LOAN_INTEREST_RATE_CAR = BigDecimal.valueOf(10.0); // 10% per annum

    // Loan Related
    public static final BigDecimal MINIMUM_LOAN_AMOUNT = BigDecimal.valueOf(50000);
    public static final BigDecimal MAXIMUM_LOAN_AMOUNT = BigDecimal.valueOf(10000000);
    public static final Integer MINIMUM_LOAN_TENURE_MONTHS = 6;
    public static final Integer MAXIMUM_LOAN_TENURE_MONTHS = 360; // 30 years

    // Card Related
    public static final Integer CARD_EXPIRY_YEARS = 5;
    public static final BigDecimal DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(50000);
    public static final BigDecimal MINIMUM_CREDIT_LIMIT = BigDecimal.valueOf(10000);
    public static final BigDecimal MAXIMUM_CREDIT_LIMIT = BigDecimal.valueOf(500000);

    // Status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_BLOCKED = "BLOCKED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    // Roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    // Messages
    public static final String MSG_SUCCESS = "Operation completed successfully";
    public static final String MSG_ACCOUNT_CREATED = "Account created successfully";
    public static final String MSG_TRANSACTION_SUCCESS = "Transaction completed successfully";
    public static final String MSG_INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String MSG_INVALID_ACCOUNT = "Invalid account number";
    public static final String MSG_ACCOUNT_BLOCKED = "Account is blocked";
    public static final String MSG_UNAUTHORIZED = "Unauthorized access";

    // Regex Patterns
    public static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String REGEX_PHONE = "^[+]?[0-9]{10,13}$";
    public static final String REGEX_AADHAR = "^[0-9]{4}-[0-9]{4}-[0-9]{4}$";
    public static final String REGEX_PAN = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";
    public static final String REGEX_IFSC = "^[A-Z]{4}0[A-Z0-9]{6}$";

    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";
    public static final String DISPLAY_DATETIME_FORMAT = "dd MMM yyyy, hh:mm a";

    // Pagination
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer MAX_PAGE_SIZE = 100;

    // File Upload
    public static final Long MAX_FILE_SIZE = 5242880L; // 5MB
    public static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/png", "application/pdf"};

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
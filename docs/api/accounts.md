# Accounts API Documentation

## Overview

The Accounts API provides comprehensive functionality for managing bank accounts, including creation, retrieval, updates, and closure of various account types.

## Base URL

```
https://api.bankingsystem.com/api/v1
```

## Authentication

All account endpoints require authentication via Bearer token:

```
Authorization: Bearer {accessToken}
```

## Account Types

- **SAVINGS** - Standard savings account with interest
- **CHECKING** - Standard checking account for daily transactions
- **BUSINESS** - Business account with higher limits
- **INVESTMENT** - Investment account with portfolio management
- **FIXED_DEPOSIT** - Fixed deposit account with locked-in rates

## Endpoints

### 1. Create Account

Create a new bank account for the authenticated user.

**Endpoint:** `POST /api/accounts`

**Request Body:**
```json
{
  "accountType": "SAVINGS",
  "currency": "USD",
  "initialDeposit": 1000.00,
  "nickname": "Emergency Fund"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Account created successfully",
  "data": {
    "account": {
      "id": "acc_1a2b3c4d",
      "accountNumber": "1234567890",
      "accountType": "SAVINGS",
      "currency": "USD",
      "balance": 1000.00,
      "availableBalance": 1000.00,
      "status": "ACTIVE",
      "nickname": "Emergency Fund",
      "interestRate": 2.5,
      "overdraftLimit": 0,
      "createdAt": "2025-11-09T10:00:00Z",
      "updatedAt": "2025-11-09T10:00:00Z"
    }
  }
}
```

**Validation Rules:**
- `accountType`: Required, must be valid account type
- `currency`: Required, must be supported currency (USD, EUR, GBP, INR)
- `initialDeposit`: Optional for SAVINGS/CHECKING, required for others, minimum $100
- `nickname`: Optional, max 50 characters

**Error Codes:**
- `ACC001`: Invalid account type
- `ACC002`: Insufficient initial deposit
- `ACC003`: Currency not supported
- `ACC004`: Maximum account limit reached (10 per user)

---

### 2. Get All Accounts

Retrieve all accounts belonging to the authenticated user.

**Endpoint:** `GET /api/accounts`

**Query Parameters:**
- `status` (optional): Filter by status (ACTIVE, INACTIVE, FROZEN, CLOSED)
- `type` (optional): Filter by account type
- `page` (optional): Page number (default: 1)
- `limit` (optional): Results per page (default: 10, max: 50)

**Example:** `GET /api/accounts?status=ACTIVE&type=SAVINGS&page=1&limit=10`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "accounts": [
      {
        "id": "acc_1a2b3c4d",
        "accountNumber": "1234567890",
        "accountType": "SAVINGS",
        "currency": "USD",
        "balance": 5000.00,
        "availableBalance": 5000.00,
        "status": "ACTIVE",
        "nickname": "Emergency Fund",
        "interestRate": 2.5,
        "createdAt": "2025-11-09T10:00:00Z"
      },
      {
        "id": "acc_5e6f7g8h",
        "accountNumber": "0987654321",
        "accountType": "CHECKING",
        "currency": "USD",
        "balance": 2500.00,
        "availableBalance": 2500.00,
        "status": "ACTIVE",
        "nickname": "Daily Expenses",
        "overdraftLimit": 500.00,
        "createdAt": "2025-10-15T09:30:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 1,
      "totalItems": 2,
      "itemsPerPage": 10
    }
  }
}
```

---

### 3. Get Account by ID

Retrieve detailed information for a specific account.

**Endpoint:** `GET /api/accounts/:accountId`

**Path Parameters:**
- `accountId`: The unique account identifier

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "account": {
      "id": "acc_1a2b3c4d",
      "accountNumber": "1234567890",
      "accountType": "SAVINGS",
      "currency": "USD",
      "balance": 5000.00,
      "availableBalance": 5000.00,
      "holdAmount": 0.00,
      "status": "ACTIVE",
      "nickname": "Emergency Fund",
      "interestRate": 2.5,
      "overdraftLimit": 0,
      "minimumBalance": 500.00,
      "openingDate": "2025-11-09T10:00:00Z",
      "lastTransactionDate": "2025-11-09T15:30:00Z",
      "createdAt": "2025-11-09T10:00:00Z",
      "updatedAt": "2025-11-09T15:30:00Z",
      "owner": {
        "id": "usr_123abc",
        "name": "John Doe",
        "email": "john@example.com"
      }
    }
  }
}
```

**Error Codes:**
- `ACC005`: Account not found
- `ACC006`: Access denied (not account owner)

---

### 4. Get Account Balance

Get current balance information for an account.

**Endpoint:** `GET /api/accounts/:accountId/balance`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "accountId": "acc_1a2b3c4d",
    "accountNumber": "1234567890",
    "balance": 5000.00,
    "availableBalance": 4800.00,
    "holdAmount": 200.00,
    "currency": "USD",
    "lastUpdated": "2025-11-09T15:30:00Z"
  }
}
```

---

### 5. Update Account

Update account details such as nickname or settings.

**Endpoint:** `PATCH /api/accounts/:accountId`

**Request Body:**
```json
{
  "nickname": "Vacation Fund",
  "overdraftLimit": 1000.00,
  "alertThreshold": 500.00
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Account updated successfully",
  "data": {
    "account": {
      "id": "acc_1a2b3c4d",
      "accountNumber": "1234567890",
      "nickname": "Vacation Fund",
      "overdraftLimit": 1000.00,
      "alertThreshold": 500.00,
      "updatedAt": "2025-11-09T16:00:00Z"
    }
  }
}
```

**Updatable Fields:**
- `nickname`: String (max 50 chars)
- `overdraftLimit`: Number (CHECKING/BUSINESS only)
- `alertThreshold`: Number (balance alert threshold)
- `settings`: Object (account preferences)

**Error Codes:**
- `ACC007`: Invalid update operation
- `ACC008`: Field not updatable for this account type

---

### 6. Freeze Account

Temporarily freeze an account to prevent transactions.

**Endpoint:** `POST /api/accounts/:accountId/freeze`

**Request Body:**
```json
{
  "reason": "SUSPICIOUS_ACTIVITY",
  "notes": "Multiple failed login attempts detected"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Account frozen successfully",
  "data": {
    "accountId": "acc_1a2b3c4d",
    "status": "FROZEN",
    "frozenAt": "2025-11-09T16:30:00Z",
    "reason": "SUSPICIOUS_ACTIVITY"
  }
}
```

**Freeze Reasons:**
- `SUSPICIOUS_ACTIVITY`
- `LOST_CARD`
- `USER_REQUEST`
- `COMPLIANCE_REVIEW`
- `FRAUD_INVESTIGATION`

---

### 7. Unfreeze Account

Remove freeze from an account.

**Endpoint:** `POST /api/accounts/:accountId/unfreeze`

**Request Body:**
```json
{
  "verificationCode": "123456"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Account unfrozen successfully",
  "data": {
    "accountId": "acc_1a2b3c4d",
    "status": "ACTIVE",
    "unfrozenAt": "2025-11-09T17:00:00Z"
  }
}
```

---

### 8. Close Account

Permanently close a bank account.

**Endpoint:** `DELETE /api/accounts/:accountId`

**Request Body:**
```json
{
  "reason": "NO_LONGER_NEEDED",
  "transferAccountId": "acc_5e6f7g8h",
  "password": "SecurePass123!"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Account closed successfully",
  "data": {
    "accountId": "acc_1a2b3c4d",
    "status": "CLOSED",
    "closedAt": "2025-11-09T17:30:00Z",
    "finalBalance": 0.00,
    "transferredTo": "acc_5e6f7g8h"
  }
}
```

**Requirements:**
- Account balance must be zero OR `transferAccountId` provided
- Password confirmation required
- No pending transactions
- Account must be owned by authenticated user

**Error Codes:**
- `ACC009`: Cannot close account with balance
- `ACC010`: Pending transactions exist
- `ACC011`: Invalid transfer account

---

### 9. Get Account Statement

Retrieve account statement for a specific period.

**Endpoint:** `GET /api/accounts/:accountId/statement`

**Query Parameters:**
- `startDate`: Start date (ISO 8601 format)
- `endDate`: End date (ISO 8601 format)
- `format`: Response format (json, pdf, csv) - default: json

**Example:** `GET /api/accounts/acc_1a2b3c4d/statement?startDate=2025-10-01&endDate=2025-10-31&format=json`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "statement": {
      "accountNumber": "1234567890",
      "period": {
        "from": "2025-10-01",
        "to": "2025-10-31"
      },
      "openingBalance": 4500.00,
      "closingBalance": 5000.00,
      "totalCredits": 2000.00,
      "totalDebits": 1500.00,
      "currency": "USD",
      "transactions": [
        {
          "date": "2025-10-15",
          "description": "Salary Deposit",
          "reference": "TXN123456",
          "debit": null,
          "credit": 2000.00,
          "balance": 6500.00
        },
        {
          "date": "2025-10-20",
          "description": "Rent Payment",
          "reference": "TXN123457",
          "debit": 1500.00,
          "credit": null,
          "balance": 5000.00
        }
      ]
    }
  }
}
```

---

### 10. Get Account Limits

Retrieve transaction limits for an account.

**Endpoint:** `GET /api/accounts/:accountId/limits`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "limits": {
      "daily": {
        "withdrawal": {
          "limit": 5000.00,
          "used": 1200.00,
          "remaining": 3800.00
        },
        "transfer": {
          "limit": 10000.00,
          "used": 2000.00,
          "remaining": 8000.00
        }
      },
      "monthly": {
        "withdrawal": {
          "limit": 50000.00,
          "used": 15000.00,
          "remaining": 35000.00
        },
        "transfer": {
          "limit": 100000.00,
          "used": 25000.00,
          "remaining": 75000.00
        }
      },
      "perTransaction": {
        "withdrawal": 2000.00,
        "transfer": 5000.00
      }
    }
  }
}
```

---

## Account Status Flow

```
PENDING → ACTIVE → FROZEN → ACTIVE
                 ↓
               CLOSED
```

- **PENDING**: Newly created, awaiting verification
- **ACTIVE**: Fully operational
- **INACTIVE**: Dormant (no transactions for 6+ months)
- **FROZEN**: Temporarily suspended
- **CLOSED**: Permanently closed

## Interest Calculation

### Savings Account
- Interest calculated daily
- Credited monthly
- Rate: 2.5% APY (variable)
- Formula: `Daily Interest = (Balance × Annual Rate) / 365`

### Fixed Deposit
- Interest calculated based on term
- Locked-in rate at account creation
- Early withdrawal penalties apply
- Compounding: Monthly, Quarterly, or Annually

**Example Calculation:**
```
Principal: $10,000
Rate: 5% APY
Term: 1 year
Compounding: Monthly

Monthly Interest = Principal × (Rate / 12)
Total Interest = Principal × ((1 + Rate/12)^12 - 1)
Maturity Amount = $10,511.62
```

## Account Fees

| Account Type | Monthly Fee | Minimum Balance | Transaction Fee |
|--------------|-------------|-----------------|-----------------|
| SAVINGS | $0 | $500 | $0 |
| CHECKING | $5 (waived if balance > $1000) | $0 | $0 |
| BUSINESS | $15 | $1000 | $0.10/transaction |
| INVESTMENT | 0.5% of AUM annually | $5000 | Varies |
| FIXED_DEPOSIT | $0 | Varies | Early withdrawal: 1% |

## Security Features

### Account Protection
- Real-time fraud monitoring
- Unusual activity alerts
- Transaction notifications
- Biometric authentication option
- Device authorization

### Compliance
- KYC (Know Your Customer) verification
- AML (Anti-Money Laundering) checks
- Tax reporting (Form 1099-INT)
- FDIC insured up to $250,000

## Webhooks

Subscribe to account events:

**Events:**
- `account.created`
- `account.updated`
- `account.frozen`
- `account.unfrozen`
- `account.closed`
- `account.balance.low`
- `account.limit.reached`

**Webhook Payload Example:**
```json
{
  "event": "account.balance.low",
  "timestamp": "2025-11-09T18:00:00Z",
  "data": {
    "accountId": "acc_1a2b3c4d",
    "accountNumber": "1234567890",
    "balance": 450.00,
    "threshold": 500.00
  }
}
```

## Rate Limits

- **Read operations**: 100 requests/minute
- **Write operations**: 30 requests/minute
- **Statement generation**: 10 requests/hour

## Best Practices

1. **Cache account data** - Reduce API calls for frequently accessed data
2. **Use webhooks** - Subscribe to events instead of polling
3. **Handle errors gracefully** - Implement retry logic with exponential backoff
4. **Validate before submission** - Client-side validation reduces errors
5. **Monitor limits** - Track daily/monthly limits proactively
6. **Secure storage** - Never log or store account numbers in plain text
7. **Test in sandbox** - Use test environment before production
8. **Implement idempotency** - Use idempotency keys for create operations
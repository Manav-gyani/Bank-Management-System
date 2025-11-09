# Transactions API Documentation

## Overview

The Transactions API enables users to perform various financial operations including transfers, deposits, withdrawals, and bill payments, as well as query transaction history.

## Base URL

```
https://api.bankingsystem.com/api/v1
```

## Transaction Types

- **TRANSFER** - Between accounts (internal/external)
- **DEPOSIT** - Add funds to account
- **WITHDRAWAL** - Remove funds from account
- **PAYMENT** - Bill payment or merchant payment
- **REFUND** - Transaction reversal
- **FEE** - Service charges
- **INTEREST** - Interest credit

## Endpoints

### 1. Create Transaction (Transfer)

Initiate a funds transfer between accounts.

**Endpoint:** `POST /api/transactions/transfer`

**Request Body:**
```json
{
  "fromAccountId": "acc_1a2b3c4d",
  "toAccountId": "acc_5e6f7g8h",
  "amount": 500.00,
  "currency": "USD",
  "description": "Rent payment",
  "reference": "RENT-NOV-2025",
  "scheduled": false,
  "scheduledDate": null
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Transfer completed successfully",
  "data": {
    "transaction": {
      "id": "txn_9i8u7y6t",
      "transactionNumber": "TXN20251109001",
      "type": "TRANSFER",
      "status": "COMPLETED",
      "fromAccount": {
        "id": "acc_1a2b3c4d",
        "accountNumber": "1234567890",
        "balance": 4500.00
      },
      "toAccount": {
        "id": "acc_5e6f7g8h",
        "accountNumber": "0987654321",
        "balance": 3000.00
      },
      "amount": 500.00,
      "currency": "USD",
      "description": "Rent payment",
      "reference": "RENT-NOV-2025",
      "fee": 0.00,
      "timestamp": "2025-11-09T10:30:00Z",
      "completedAt": "2025-11-09T10:30:01Z"
    }
  }
}
```

**Validation Rules:**
- Accounts must exist and be active
- Sufficient balance in source account
- Amount must be positive and within limits
- Currency must match account currency
- User must own the source account

**Error Codes:**
- `TXN001`: Insufficient funds
- `TXN002`: Account not found
- `TXN003`: Account frozen or inactive
- `TXN004`: Daily limit exceeded
- `TXN005`: Invalid amount
- `TXN006`: Currency mismatch

---

### 2. External Transfer

Transfer funds to an external bank account.

**Endpoint:** `POST /api/transactions/transfer/external`

**Request Body:**
```json
{
  "fromAccountId": "acc_1a2b3c4d",
  "beneficiary": {
    "name": "Jane Smith",
    "accountNumber": "9876543210",
    "routingNumber": "021000021",
    "bankName": "External Bank",
    "accountType": "CHECKING"
  },
  "amount": 1000.00,
  "currency": "USD",
  "description": "Payment to Jane",
  "purpose": "PERSONAL_TRANSFER"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "External transfer initiated",
  "data": {
    "transaction": {
      "id": "txn_ext_123abc",
      "transactionNumber": "TXN20251109002",
      "type": "EXTERNAL_TRANSFER",
      "status": "PENDING",
      "fromAccount": {
        "id": "acc_1a2b3c4d",
        "accountNumber": "1234567890"
      },
      "beneficiary": {
        "name": "Jane Smith",
        "accountNumber": "9876543210",
        "bankName": "External Bank"
      },
      "amount": 1000.00,
      "currency": "USD",
      "fee": 5.00,
      "estimatedCompletion": "2025-11-11T10:30:00Z",
      "timestamp": "2025-11-09T10:30:00Z"
    }
  }
}
```

**Processing Time:**
- ACH Transfer: 1-3 business days
- Wire Transfer: Same day (if before 2 PM EST)
- International: 3-5 business days

**Fees:**
- ACH: $0 (up to 3 free/month, then $1)
- Wire (Domestic): $25
- Wire (International): $45

---

### 3. Deposit

Deposit funds into an account.

**Endpoint:** `POST /api/transactions/deposit`

**Request Body:**
```json
{
  "accountId": "acc_1a2b3c4d",
  "amount": 2000.00,
  "currency": "USD",
  "method": "CASH",
  "location": "Branch A",
  "reference": "DEP123456"
}
```

**Deposit Methods:**
- `CASH`: Cash deposit at branch
- `CHECK`: Check deposit
- `MOBILE_CHECK`: Mobile check deposit
- `WIRE`: Wire transfer
- `DIRECT_DEPOSIT`: Payroll or benefits

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Deposit completed successfully",
  "data": {
    "transaction": {
      "id": "txn_dep_456def",
      "type": "DEPOSIT",
      "status": "COMPLETED",
      "accountId": "acc_1a2b3c4d",
      "amount": 2000.00,
      "method": "CASH",
      "availableDate": "2025-11-09T10:30:00Z",
      "timestamp": "2025-11-09T10:30:00Z"
    }
  }
}
```

**Hold Periods:**
- Cash: Immediate
- Check: 1-2 business days
- Mobile Check: 2-3 business days
- Wire: Same day

---

### 4. Withdrawal

Withdraw funds from an account.

**Endpoint:** `POST /api/transactions/withdrawal`

**Request Body:**
```json
{
  "accountId": "acc_1a2b3c4d",
  "amount": 300.00,
  "currency": "USD",
  "method": "ATM",
  "location": "ATM-NYC-001",
  "pin": "encrypted_pin_hash"
}
```

**Withdrawal Methods:**
- `ATM`: ATM withdrawal
- `BRANCH`: Teller withdrawal
- `CHECK`: Check issuance
- `DEBIT_CARD`: Point of sale

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Withdrawal completed successfully",
  "data": {
    "transaction": {
      "id": "txn_wth_789ghi",
      "type": "WITHDRAWAL",
      "status": "COMPLETED",
      "accountId": "acc_1a2b3c4d",
      "amount": 300.00,
      "fee": 2.00,
      "method": "ATM",
      "timestamp": "2025-11-09T11:00:00Z"
    }
  }
}
```

**ATM Fees:**
- In-network: $0
- Out-of-network: $2.50
- International: $5.00

---

### 5. Bill Payment

Pay bills to registered payees.

**Endpoint:** `POST /api/transactions/payment`

**Request Body:**
```json
{
  "fromAccountId": "acc_1a2b3c4d",
  "payeeId": "payee_elec_001",
  "amount": 150.00,
  "currency": "USD",
  "paymentDate": "2025-11-15",
  "accountNumber": "1234567890",
  "memo": "November electricity bill"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Payment scheduled successfully",
  "data": {
    "transaction": {
      "id": "txn_pay_012jkl",
      "type": "PAYMENT",
      "status": "SCHEDULED",
      "fromAccount": {
        "id": "acc_1a2b3c4d",
        "accountNumber": "1234567890"
      },
      "payee": {
        "id": "payee_elec_001",
        "name": "Electric Company",
        "type": "UTILITY"
      },
      "amount": 150.00,
      "scheduledDate": "2025-11-15",
      "estimatedDelivery": "2025-11-17",
      "timestamp": "2025-11-09T11:30:00Z"
    }
  }
}
```

---

### 6. Get Transaction by ID

Retrieve details of a specific transaction.

**Endpoint:** `GET /api/transactions/:transactionId`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "transaction": {
      "id": "txn_9i8u7y6t",
      "transactionNumber": "TXN20251109001",
      "type": "TRANSFER",
      "status": "COMPLETED",
      "fromAccount": {
        "id": "acc_1a2b3c4d",
        "accountNumber": "****7890",
        "holder": "John Doe"
      },
      "toAccount": {
        "id": "acc_5e6f7g8h",
        "accountNumber": "****4321",
        "holder": "Jane Doe"
      },
      "amount": 500.00,
      "currency": "USD",
      "description": "Rent payment",
      "reference": "RENT-NOV-2025",
      "fee": 0.00,
      "balanceAfter": 4500.00,
      "timestamp": "2025-11-09T10:30:00Z",
      "completedAt": "2025-11-09T10:30:01Z",
      "metadata": {
        "ipAddress": "192.168.1.1",
        "device": "Chrome on Windows",
        "location": "New York, US"
      }
    }
  }
}
```

---

### 7. Get Transaction History

Retrieve transaction history for an account.

**Endpoint:** `GET /api/transactions`

**Query Parameters:**
- `accountId`: Filter by account (required)
- `type`: Filter by transaction type
- `status`: Filter by status (PENDING, COMPLETED, FAILED, CANCELLED)
- `startDate`: Start date (ISO 8601)
- `endDate`: End date (ISO 8601)
- `minAmount`: Minimum amount
- `maxAmount`: Maximum amount
- `search`: Search in description/reference
- `page`: Page number (default: 1)
- `limit`: Results per page (default: 20, max: 100)
- `sortBy`: Sort field (date, amount)
- `sortOrder`: asc or desc (default: desc)

**Example:**
```
GET /api/transactions?accountId=acc_1a2b3c4d&type=TRANSFER&startDate=2025-10-01&endDate=2025-10-31&page=1&limit=20
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "transactions": [
      {
        "id": "txn_9i8u7y6t",
        "transactionNumber": "TXN20251109001",
        "type": "TRANSFER",
        "status": "COMPLETED",
        "amount": 500.00,
        "currency": "USD",
        "description": "Rent payment",
        "counterparty": "Jane Doe",
        "balanceAfter": 4500.00,
        "timestamp": "2025-11-09T10:30:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "totalItems": 87,
      "itemsPerPage": 20
    },
    "summary": {
      "totalDebits": 2500.00,
      "totalCredits": 3000.00,
      "netChange": 500.00
    }
  }
}
```

---

### 8. Search Transactions

Advanced transaction search with multiple filters.

**Endpoint:** `POST /api/transactions/search`

**Request Body:**
```json
{
  "accountId": "acc_1a2b3c4d",
  "filters": {
    "types": ["TRANSFER", "PAYMENT"],
    "status": ["COMPLETED"],
    "dateRange": {
      "from": "2025-10-01",
      "to": "2025-10-31"
    },
    "amountRange": {
      "min": 100,
      "max": 1000
    },
    "keywords": ["rent", "utilities"]
  },
  "page": 1,
  "limit": 20
}
```

---

### 9. Cancel Transaction

Cancel a pending or scheduled transaction.

**Endpoint:** `POST /api/transactions/:transactionId/cancel`

**Request Body:**
```json
{
  "reason": "INCORRECT_AMOUNT",
  "notes": "Need to resend with correct amount"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Transaction cancelled successfully",
  "data": {
    "transactionId": "txn_pay_012jkl",
    "status": "CANCELLED",
    "cancelledAt": "2025-11-09T12:00:00Z",
    "refundAmount": 150.00
  }
}
```

**Cancellable Statuses:**
- PENDING
- SCHEDULED
- PROCESSING (within 5 minutes)

**Error Codes:**
- `TXN007`: Transaction cannot be cancelled
- `TXN008`: Transaction already completed

---

### 10. Request Refund

Request a refund for a completed transaction.

**Endpoint:** `POST /api/transactions/:transactionId/refund`

**Request Body:**
```json
{
  "reason": "DUPLICATE_PAYMENT",
  "amount": 150.00,
  "description": "Accidental duplicate payment"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Refund request submitted",
  "data": {
    "refundRequest": {
      "id": "ref_345mno",
      "originalTransaction": "txn_pay_012jkl",
      "amount": 150.00,
      "status": "PENDING_REVIEW",
      "expectedResolution": "2025-11-12T12:00:00Z",
      "createdAt": "2025-11-09T12:30:00Z"
    }
  }
}
```

**Refund Reasons:**
- DUPLICATE_PAYMENT
- SERVICE_NOT_RECEIVED
- INCORRECT_AMOUNT
- UNAUTHORIZED_TRANSACTION
- MERCHANT_ERROR

**Processing Time:** 3-5 business days

---

### 11. Schedule Recurring Transaction

Set up recurring payments or transfers.

**Endpoint:** `POST /api/transactions/recurring`

**Request Body:**
```json
{
  "fromAccountId": "acc_1a2b3c4d",
  "toAccountId": "acc_5e6f7g8h",
  "amount": 500.00,
  "currency": "USD",
  "description": "Monthly rent",
  "frequency": "MONTHLY",
  "startDate": "2025-11-15",
  "endDate": "2026-11-15",
  "dayOfMonth": 15
}
```

**Frequency Options:**
- `DAILY`
- `WEEKLY`
- `BIWEEKLY`
- `MONTHLY`
- `QUARTERLY`
- `ANNUALLY`

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Recurring transaction created",
  "data": {
    "recurringTransaction": {
      "id": "rec_678pqr",
      "type": "RECURRING_TRANSFER",
      "amount": 500.00,
      "frequency": "MONTHLY",
      "nextExecution": "2025-11-15T09:00:00Z",
      "endDate": "2026-11-15",
      "status": "ACTIVE",
      "executionCount": 0,
      "createdAt": "2025-11-09T13:00:00Z"
    }
  }
}
```

---

### 12. Export Transactions

Export transaction history in various formats.

**Endpoint:** `GET /api/transactions/export`

**Query Parameters:**
- `accountId`: Account to export
- `format`: csv, pdf, xlsx, qbo, ofx
- `startDate`: Start date
- `endDate`: End date

**Response:** File download
- CSV: Comma-separated values
- PDF: Formatted statement
- XLSX: Excel spreadsheet
- QBO: QuickBooks format
- OFX: Open Financial Exchange format

---

## Transaction Status Flow

```
INITIATED → PENDING → PROCESSING → COMPLETED
                   ↓              ↓
                FAILED        CANCELLED
                   ↓
              REFUNDED
```

**Status Descriptions:**
- **INITIATED**: Transaction created, validation in progress
- **PENDING**: Awaiting execution (scheduled transactions)
- **PROCESSING**: Being processed by payment network
- **COMPLETED**: Successfully completed
- **FAILED**: Transaction failed (insufficient funds, etc.)
- **CANCELLED**: Cancelled by user or system
- **REFUNDED**: Transaction reversed

---

## Transaction Limits

### Daily Limits (Default)

| Account Type | ATM Withdrawal | Transfer | Bill Payment |
|--------------|----------------|----------|--------------|
| SAVINGS | $1,000 | $5,000 | $2,000 |
| CHECKING | $1,500 | $10,000 | $5,000 |
| BUSINESS | $5,000 | $50,000 | $25,000 |

### Monthly Limits

| Account Type | Total Transfers | External Transfers |
|--------------|-----------------|-------------------|
| SAVINGS | $20,000 | $10,000 |
| CHECKING | $50,000 | $25,000 |
| BUSINESS | $500,000 | $250,000 |

### Per Transaction Limits

- Minimum: $0.01
- Maximum internal transfer: $10,000 (can be increased)
- Maximum external transfer: $5,000
- Maximum ATM withdrawal: $500

---

## Fees Structure

| Transaction Type | Fee | Notes |
|------------------|-----|-------|
| Internal Transfer | Free | Between own accounts |
| External ACH | $1 after 3 free | Per month |
| Wire (Domestic) | $25 | Same day |
| Wire (International) | $45 | 3-5 days |
| ATM (In-network) | Free | Unlimited |
| ATM (Out-of-network) | $2.50 | Per transaction |
| Overdraft | $35 | Per occurrence |
| Stop Payment | $25 | Per check |
| Returned Item | $30 | NSF fee |

---

## Security Features

### Fraud Detection
- Real-time transaction monitoring
- Velocity checks (frequency/amount)
- Geo-location verification
- Device fingerprinting
- Behavioral analysis

### Transaction Verification
- Two-factor authentication for large amounts
- Biometric confirmation option
- Email/SMS notifications
- Transaction confirmation codes

### Dispute Resolution
- 60-day dispute window
- Zero liability for unauthorized transactions
- Investigation timeline: 10 business days
- Provisional credit during investigation

---

## Webhooks

Subscribe to transaction events:

**Events:**
- `transaction.created`
- `transaction.completed`
- `transaction.failed`
- `transaction.cancelled`
- `transaction.refunded`
- `recurring.executed`
- `recurring.failed`
- `limit.approaching`

**Example Payload:**
```json
{
  "event": "transaction.completed",
  "timestamp": "2025-11-09T10:30:01Z",
  "data": {
    "transactionId": "txn_9i8u7y6t",
    "accountId": "acc_1a2b3c4d",
    "amount": 500.00,
    "type": "TRANSFER",
    "status": "COMPLETED"
  }
}
```

---

## Best Practices

1. **Idempotency**: Use idempotency keys to prevent duplicate transactions
2. **Error Handling**: Implement retry logic with exponential backoff
3. **Balance Checks**: Verify balance before initiating transactions
4. **Validation**: Validate all inputs client-side before API calls
5. **Notifications**: Subscribe to webhooks for real-time updates
6. **Reconciliation**: Regular reconciliation of transaction records
7. **Audit Logs**: Maintain comprehensive audit trails
8. **Testing**: Use sandbox environment for testing
9. **Rate Limiting**: Respect API rate limits
10. **Security**: Never log sensitive transaction details
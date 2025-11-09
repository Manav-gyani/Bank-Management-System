📘 Database Schema Documentation
Overview

The banking system uses MongoDB as its primary NoSQL database.
This document describes the collections, their fields, relationships, indexes, and data design considerations used to implement the banking platform.

Unlike relational databases, MongoDB stores data in collections instead of tables and documents instead of rows.
Each document is a flexible JSON-like object that can contain nested structures, arrays, and dynamic fields.

🗂️ Database Configuration

Database Name: banking_system

Database Type: MongoDB (Document-oriented, NoSQL)

Default Encoding: UTF-8

Primary Identifier: Each document uses _id (ObjectId) as the unique identifier.

Relationships: Implemented via references (ObjectId) or embedded documents.

Storage Engine: WiredTiger

Indexes: Created for high-frequency query fields such as user references, account numbers, and timestamps.

🧭 Entity Relationship Overview

Although MongoDB is schema-less, we define a logical relationship structure between collections:

Users ───< Accounts ───< Transactions
│            │
│            └──< Transaction Limits
│
├──< Sessions
├──< Beneficiaries
├──< Recurring Transactions
├──< Notifications
├──< Audit Logs
└──< Fraud Alerts

🧍‍♂️ 1. Users Collection

Purpose:
Stores all user-related information, including authentication credentials, personal details, and security preferences.

Collection Name: users

Key Fields:

_id: Unique ObjectId (auto-generated)

username: Unique username

email: Unique email address for login and communication

passwordHash: Encrypted password string

firstName, lastName: Personal identification

phoneNumber: Optional contact number

dateOfBirth: Date of birth

address: Embedded object containing:

street, city, state, zipCode, country

status: Enum – PENDING_VERIFICATION, ACTIVE, SUSPENDED, CLOSED

emailVerified, phoneVerified: Boolean flags for verification

twoFactorEnabled: Boolean flag for 2FA

twoFactorSecret: 2FA secret key

lastLoginAt: Last login timestamp

failedLoginAttempts: Counter for security

lockedUntil: Timestamp for temporary account lock

createdAt, updatedAt: Metadata timestamps

Indexes:

email (unique)

username (unique)

status

createdAt (descending)

Relationships:

One-to-many with accounts, sessions, beneficiaries, notifications.

💳 2. Accounts Collection

Purpose:
Represents bank accounts owned by users, storing balances and account configurations.

Collection Name: accounts

Key Fields:

_id: Unique ObjectId

userId: Reference to users._id

accountNumber: Unique account identifier (string)

accountType: Enum – SAVINGS, CHECKING, BUSINESS, INVESTMENT, FIXED_DEPOSIT

balance: Current balance

availableBalance: Balance available for transactions

holdAmount: Amount on hold

currency: Default USD

nickname: Optional custom label

interestRate: Applicable interest rate

overdraftLimit, minimumBalance: Financial constraints

status: Enum – PENDING, ACTIVE, INACTIVE, FROZEN, CLOSED

openingDate, closingDate: Account lifecycle

lastTransactionDate: Last activity date

createdAt, updatedAt: Metadata

Indexes:

userId

accountNumber (unique)

status

Composite: (userId, status)

Relationships:

One-to-many with transactions

One-to-one with transaction_limits

Referenced by beneficiaries, fraud_alerts, and recurring_transactions

💸 3. Transactions Collection

Purpose:
Stores all financial transaction details, including transfers, deposits, and withdrawals.

Collection Name: transactions

Key Fields:

_id: Unique ObjectId

transactionNumber: Unique string identifier (e.g., TXN202511010001)

type: Enum – TRANSFER, DEPOSIT, WITHDRAWAL, PAYMENT, REFUND, FEE, INTEREST, EXTERNAL_TRANSFER

fromAccountId: Reference to source accounts._id

toAccountId: Reference to destination accounts._id

amount: Positive decimal value

currency: Transaction currency (default USD)

fee: Optional processing fee

status: Enum – INITIATED, PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED

description: Text description

reference: External reference or note

external: Embedded object with:

bankName, accountNumber, routingNumber

fromBalanceBefore / After

toBalanceBefore / After

method: Transaction channel – ATM, BRANCH, ONLINE, MOBILE

location, ipAddress, deviceInfo

initiatedAt, processedAt, completedAt

createdAt, updatedAt

Indexes:

fromAccountId

toAccountId

status

type

createdAt (descending)

transactionNumber (unique)

Relationships:

Many-to-one with accounts

One-to-one optional link with fraud_alerts

🔑 4. Sessions Collection

Purpose:
Tracks active login sessions and tokens for authentication.

Collection Name: sessions

Key Fields:

_id: ObjectId

userId: Reference to users._id

refreshToken: Unique token for session renewal

accessTokenHash: Hashed access token

deviceType, deviceName, browser, os

ipAddress, location

isActive: Session status flag

lastActivity, expiresAt

createdAt

Indexes:

userId

refreshToken (unique)

expiresAt

Composite: (userId, isActive)

Relationships:

Many-to-one with users

👥 5. Beneficiaries Collection

Purpose:
Stores users’ saved beneficiary accounts for quick money transfers.

Collection Name: beneficiaries

Key Fields:

_id: ObjectId

userId: Reference to users._id

name: Beneficiary’s full name

accountNumber: Target account

bankName: Bank name for external transfers

routingNumber: Routing or IFSC code

accountType: Account category

isInternal: Boolean – true if beneficiary is another internal account

internalAccountId: Reference to internal accounts._id (optional)

nickname: Custom alias

isVerified: Verification status

isActive: Active or inactive flag

createdAt, updatedAt

Indexes:

Composite unique: (userId, accountNumber)

accountNumber

userId

Relationships:

Many-to-one with users

Optional link to internal accounts

🔁 6. Recurring Transactions Collection

Purpose:
Handles automated scheduled transactions such as recurring bill payments or transfers.

Collection Name: recurring_transactions

Key Fields:

_id: ObjectId

userId: Reference to users._id

fromAccountId, toAccountId: Account references

amount: Positive decimal

currency: Default USD

description: Optional text

frequency: Enum – DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALLY

dayOfMonth / dayOfWeek: Scheduling values

startDate, endDate, nextExecutionDate, lastExecutionDate

status: Enum – ACTIVE, PAUSED, COMPLETED, CANCELLED

executionCount, maxExecutions

createdAt, updatedAt

Indexes:

userId

fromAccountId

nextExecutionDate (for scheduling jobs)

📊 7. Transaction Limits Collection

Purpose:
Defines transaction and withdrawal limits per account.

Collection Name: transaction_limits

Key Fields:

_id: ObjectId

accountId: Reference to accounts._id (unique)

dailyWithdrawalLimit, dailyWithdrawalUsed

dailyTransferLimit, dailyTransferUsed

monthlyWithdrawalLimit, monthlyWithdrawalUsed

monthlyTransferLimit, monthlyTransferUsed

maxWithdrawalPerTransaction, maxTransferPerTransaction

dailyResetDate, monthlyResetDate

createdAt, updatedAt

Indexes:

accountId (unique)

🧾 8. Audit Logs Collection

Purpose:
Maintains a historical record of critical system actions for auditing and compliance.

Collection Name: audit_logs

Key Fields:

_id: ObjectId

userId: Optional reference to users._id

actorType: USER, ADMIN, or SYSTEM

action: Name of performed operation

resourceType: Type of resource affected (ACCOUNT, TRANSACTION, etc.)

resourceId: Related resource _id

description: Action summary

oldValues: JSON object of previous state

newValues: JSON object of new state

ipAddress, userAgent

status: SUCCESS or FAILURE

errorMessage: Optional error details

createdAt

Indexes:

userId

Composite: (resourceType, resourceId)

action

createdAt (descending)

🔔 9. Notifications Collection

Purpose:
Stores all notifications sent to users through various channels.

Collection Name: notifications

Key Fields:

_id: ObjectId

userId: Reference to users._id

type: Enum – EMAIL, SMS, PUSH, IN_APP

subject: Notification subject

message: Main content

status: Enum – PENDING, SENT, FAILED, READ

sentAt, readAt

relatedType, relatedId

metadata: Additional info as JSON

createdAt

Indexes:

userId, createdAt (descending)

status

type

🚨 10. Fraud Alerts Collection

Purpose:
Captures system-generated alerts for potentially fraudulent or high-risk transactions.

Collection Name: fraud_alerts

Key Fields:

_id: ObjectId

transactionId: Reference to transactions._id

accountId: Reference to accounts._id

userId: Reference to users._id

alertType: Category of alert

riskScore: Numeric (0–100)

severity: Enum – LOW, MEDIUM, HIGH, CRITICAL

description: Detailed summary

rulesTriggered: Array of triggered rule names

status: Enum – NEW, INVESTIGATING, CONFIRMED, FALSE_POSITIVE, RESOLVED

reviewedBy: Reference to reviewer users._id

reviewedAt, resolutionNotes

createdAt, updatedAt

Indexes:

accountId

status

severity

createdAt (descending)

⚙️ Operational Considerations
1. Index Optimization

MongoDB supports compound and TTL (time-to-live) indexes.
Indexes are applied to user and transaction fields frequently used in queries.

2. Data Validation

Validation rules are enforced at the application level or via MongoDB schema validation using $jsonSchema.

3. Security

All sensitive data (passwords, tokens, secrets) is stored in encrypted form.

Access control is handled by application-level middleware (e.g., users can access only their accounts).

IP and device tracking enabled for fraud detection.

4. Auditing & Logging

All critical actions are captured in the audit_logs collection for traceability.

5. Partitioning (Sharding)

For scalability, high-volume collections such as transactions and audit_logs can be sharded by userId or date ranges.

6. Backup Strategy

Use MongoDB Atlas Backups or mongodump for scheduled backups:

Full backup: mongodump --db banking_system

Incremental backup via Change Streams for real-time replication.

✅ Summary
Collection Name	Description	Key Reference
users	Stores user data and security info	Primary entity
accounts	User accounts and balances	Linked to users
transactions	Transaction history	Linked to accounts
sessions	User login sessions	Linked to users
beneficiaries	Saved beneficiary accounts	Linked to users
recurring_transactions	Automated transactions	Linked to users & accounts
transaction_limits	Daily/monthly limits	Linked to accounts
audit_logs	System audit trail	Linked to users & resources
notifications	User alerts and messages	Linked to users
fraud_alerts	Fraud detection records	Linked to transactions
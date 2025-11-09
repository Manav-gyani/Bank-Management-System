# Flow Diagrams

## Overview

This document contains detailed flow diagrams for key processes in the banking system using Mermaid syntax.

## 1. User Registration Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as Frontend
    participant API as API Gateway
    participant Auth as Auth Service
    participant DB as Database
    participant Email as Email Service
    
    User->>UI: Enter registration details
    UI->>UI: Validate input
    UI->>API: POST /api/auth/register
    API->>Auth: Forward request
    Auth->>Auth: Validate data
    Auth->>Auth: Hash password
    Auth->>DB: Check if user exists
    alt User exists
        DB-->>Auth: User found
        Auth-->>API: 409 Conflict
        API-->>UI: User already exists
        UI-->>User: Show error message
    else User doesn't exist
        DB-->>Auth: User not found
        Auth->>DB: Create user record
        Auth->>Auth: Generate verification token
        Auth->>Email: Send verification email
        Auth->>Auth: Generate JWT tokens
        Auth-->>API: 201 Created + tokens
        API-->>UI: Success response
        UI-->>User: Registration successful
        UI->>UI: Store tokens
        UI->>UI: Redirect to dashboard
    end
```

## 2. User Login Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as Frontend
    participant API as API Gateway
    participant Auth as Auth Service
    participant DB as Database
    participant Redis as Cache
    
    User->>UI: Enter credentials
    UI->>API: POST /api/auth/login
    API->>Auth: Forward request
    Auth->>DB: Find user by username
    
    alt User not found
        DB-->>Auth: No user
        Auth-->>API: 401 Unauthorized
        API-->>UI: Invalid credentials
        UI-->>User: Show error
    else User found
        DB-->>Auth: Return user
        Auth->>Auth: Verify password
        alt Password incorrect
            Auth->>DB: Increment failed attempts
            Auth-->>API: 401 Unauthorized
            API-->>UI: Invalid credentials
            UI-->>User: Show error
        else Password correct
            Auth->>DB: Check if 2FA enabled
            alt 2FA enabled
                Auth->>Auth: Generate 2FA session
                Auth->>Redis: Store session
                Auth-->>API: 200 + sessionId
                API-->>UI: 2FA required
                UI-->>User: Prompt for 2FA code
            else 2FA disabled
                Auth->>Auth: Generate JWT tokens
                Auth->>DB: Update last login
                Auth->>DB: Reset failed attempts
                Auth->>Redis: Store session
                Auth-->>API: 200 + tokens
                API-->>UI: Login successful
                UI->>UI: Store tokens
                UI-->>User: Redirect to dashboard
            end
        end
    end
```

## 3. Account Creation Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as Frontend
    participant API as API Gateway
    participant Account as Account Service
    participant DB as Database
    participant Kafka as Event Bus
    participant Notif as Notification Service
    
    User->>UI: Click "Create Account"
    UI->>UI: Show account type selection
    User->>UI: Select type & enter details
    UI->>API: POST /api/accounts
    API->>API: Validate JWT token
    API->>Account: Forward request
    Account->>Account: Validate input
    Account->>DB: Check account limit
    
    alt Limit exceeded
        DB-->>Account: Max accounts reached
        Account-->>API: 400 Bad Request
        API-->>UI: Account limit exceeded
        UI-->>User: Show error
    else Limit OK
        DB-->>Account: Can create
        Account->>Account: Generate account number
        Account->>DB: Begin transaction
        Account->>DB: Create account record
        Account->>DB: Create transaction limits
        
        alt Initial deposit > 0
            Account->>DB: Create deposit transaction
            Account->>DB: Update balance
        end
        
        Account->>DB: Commit transaction
        Account->>Kafka: Publish account.created event
        Kafka->>Notif: Consume event
        Notif->>Notif: Send welcome email
        Account-->>API: 201 Created + account
        API-->>UI: Account created
        UI-->>User: Show success + account details
    end
```

## 4. Fund Transfer Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as Frontend
    participant API as API Gateway
    participant Txn as Transaction Service
    participant Account as Account Service
    participant Fraud as Fraud Detection
    participant DB as Database
    participant Kafka as Event Bus
    participant Notif as Notification Service
    
    User->>UI: Initiate transfer
    UI->>API: POST /api/transactions/transfer
    API->>Txn: Forward request
    
    Txn->>Account: Validate source account
    Account->>DB: Check account exists & active
    alt Account invalid
        DB-->>Account: Error
        Account-->>Txn: Invalid account
        Txn-->>API: 400 Bad Request
        API-->>UI: Error response
        UI-->>User: Show error
    else Account valid
        Txn->>Account: Validate destination account
        Account->>DB: Check account exists & active
        
        Txn->>Account: Check balance
        Account->>DB: Get available balance
        alt Insufficient funds
            DB-->>Account: Balance insufficient
            Account-->>Txn: Insufficient funds
            Txn-->>API: 400 Bad Request
            API-->>UI: Insufficient funds
            UI-->>User: Show error
        else Sufficient funds
            Txn->>Account: Check transaction limits
            Account->>DB: Get limits & usage
            alt Limit exceeded
                DB-->>Account: Limit exceeded
                Account-->>Txn: Limit exceeded
                Txn-->>API: 400 Bad Request
                API-->>UI: Limit exceeded
                UI-->>User: Show error
            else Limit OK
                Txn->>Fraud: Check for fraud
                Fraud->>Fraud: Calculate risk score
                Fraud->>Fraud: Check velocity limits
                Fraud->>Fraud: Check geo-location
                
                alt High risk detected
                    Fraud-->>Txn: Risk score high
                    Txn->>DB: Create fraud alert
                    Txn->>DB: Create transaction (PENDING_REVIEW)
                    Txn->>Kafka: Publish fraud.alert event
                    Txn-->>API: 202 Accepted
                    API-->>UI: Under review
                    UI-->>User: Transaction pending review
                else Risk acceptable
                    Fraud-->>Txn: Risk score OK
                    Txn->>DB: Begin transaction
                    Txn->>DB: Create transaction record
                    Txn->>DB: Lock source account
                    Txn->>DB: Deduct from source
                    Txn->>DB: Add to destination
                    Txn->>DB: Update transaction limits
                    Txn->>DB: Update transaction status (COMPLETED)
                    Txn->>DB: Commit transaction
                    
                    Txn->>Kafka: Publish transaction.completed
                    Kafka->>Notif: Consume event
                    Notif->>Notif: Send confirmation email
                    Notif->>Notif: Send SMS alert
                    
                    Txn-->>API: 201 Created + transaction
                    API-->>UI: Transfer successful
                    UI-->>User: Show success + receipt
                end
            end
        end
    end
```

## 5. External Transfer Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as Frontend
    participant API as API Gateway
    participant Txn as Transaction Service
    participant External as External Transfer Service
    participant Bank as External Bank API
    participant DB as Database
    participant Kafka as Event Bus
    
    User->>UI: Initiate external transfer
    UI->>API: POST /api/transactions/transfer/external
    API->>Txn: Forward request
    
    Txn->>Txn: Validate beneficiary details
    Txn->>DB: Verify account balance
    Txn->>DB: Create transaction (PENDING)
    Txn->>Kafka: Publish external.transfer.initiated
    Txn-->>API: 202 Accepted
    API-->>UI: Transfer initiated
    UI-->>User: Processing (1-3 days)
    
    Kafka->>External: Consume event
    External->>External: Queue for processing
    
    Note over External: Batch processing at scheduled time
    
    External->>Bank: Initiate ACH/Wire transfer
    Bank-->>External: Transfer accepted
    External->>DB: Update transaction (PROCESSING)
    External->>Kafka: Publish status update
    
    Note over Bank: External bank processes
    
    Bank->>External: Transfer completed webhook
    External->>DB: Update transaction (COMPLETED)
    External->>Kafka: Publish transaction.completed
    Kafka->>Notif: Send completion notification
```

## 6. Recurring Payment Flow

```mermaid
flowchart TD
    A[Cron Job Runs] --> B{Check Active Recurring Transactions}
    B --> C[Load transactions due today]
    C --> D{Any transactions found?}
    D -->|No| E[End]
    D -->|Yes| F[For each transaction]
    F --> G{Check account status}
    G -->|Inactive/Frozen| H[Skip & Log]
    G -->|Active| I{Check balance}
    I -->|Insufficient| J[Mark as Failed]
    J --> K[Send notification]
    I -->|Sufficient| L[Execute transaction]
    L --> M{Success?}
    M -->|No| N[Mark as Failed]
    N --> O[Retry later]
    M -->|Yes| P[Update execution count]
    P --> Q[Calculate next execution date]
    Q --> R{Reached max executions?}
    R -->|Yes| S[Mark as COMPLETED]
    R -->|No| T[Update next execution date]
    H --> U{More transactions?}
    K --> U
    O --> U
    S --> U
    T --> U
    U -->|Yes| F
    U -->|No| E
```

## 7. Fraud Detection Flow

```mermaid
flowchart TD
    A[Transaction Initiated] --> B[Extract Transaction Data]
    B --> C[Calculate Base Risk Score]
    C --> D{Check Transaction Amount}
    D -->|> $5000| E[+30 Risk Points]
    D -->|< $5000| F[Continue]
    E --> G{Check Velocity}
    F --> G
    G -->|Multiple in short time| H[+25 Risk Points]
    G -->|Normal| I[Continue]
    H --> J{Check Location}
    I --> J
    J -->|Different country| K[+35 Risk Points]
    J -->|Unusual location| L[+20 Risk Points]
    J -->|Normal location| M[Continue]
    K --> N{Check Device}
    L --> N
    M --> N
    N -->|New device| O[+15 Risk Points]
    N -->|Known device| P[Continue]
    O --> Q{Check Time}
    P --> Q
    Q -->|Unusual hour| R[+10 Risk Points]
    Q -->|Normal hour| S[Continue]
    R --> T[Calculate Total Risk Score]
    S --> T
    T --> U{Risk Score Evaluation}
    U -->|0-30: Low| V[Allow Transaction]
    U -->|31-60: Medium| W[Require 2FA]
    U -->|61-80: High| X[Hold for Review]
    U -->|81-100: Critical| Y[Block Transaction]
    V --> Z[Log Decision]
    W --> Z
    X --> AA[Create Fraud Alert]
    Y --> AB[Create Fraud Alert]
    AA --> Z
    AB --> Z
    Z --> AC[End]
```

## 8. Password Reset Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as Frontend
    participant API as API Gateway
    participant Auth as Auth Service
    participant DB as Database
    participant Email as Email Service
    
    User->>UI: Click "Forgot Password"
    UI->>UI: Show email input
    User->>UI: Enter email
    UI->>API: POST /api/auth/password/forgot
    API->>Auth: Forward request
    Auth->>DB: Find user by email
    
    alt User not found
        DB-->>Auth: No user
        Note over Auth: Security: Don't reveal user doesn't exist
        Auth-->>API: 200 OK
        API-->>UI: Check your email
        UI-->>User: Check your email
    else User found
        DB-->>Auth: User found
        Auth->>Auth: Generate reset token
        Auth->>DB: Store token with expiry (1 hour)
        Auth->>Email: Send reset email with link
        Auth-->>API: 200 OK
        API-->>UI: Check your email
        UI-->>User: Check your email
        
        Note over User: User clicks link in email
        
        User->>UI: Click reset link
        UI->>API: GET /api/auth/password/reset/:token
        API->>Auth: Validate token
        Auth->>DB: Check token validity
        
        alt Token invalid/expired
            DB-->>Auth: Invalid
            Auth-->>API: 400 Bad Request
            API-->>UI: Invalid/expired token
            UI-->>User: Request new reset link
        else Token valid
            DB-->>Auth: Valid
            Auth-->>API: 200 OK
            API-->>UI: Show password form
            UI-->>User: Enter new password
            User->>UI: Submit new password
            UI->>API: POST /api/auth/password/reset
            API->>Auth: Forward request
            Auth->>Auth: Hash new password
            Auth->>DB: Begin transaction
            Auth->>DB: Update password
            Auth->>DB: Invalidate reset token
            Auth->>DB: Revoke all sessions
            Auth->>DB: Commit transaction
            Auth->>Email: Send confirmation email
            Auth-->>API: 200 OK
            API-->>UI: Password reset successful
            UI-->>User: Redirect to login
        end
    end
```

## 9. Account Statement Generation Flow

```mermaid
flowchart TD
    A[User Requests Statement] --> B[Receive Request]
    B --> C{Validate Date Range}
    C -->|Invalid| D[Return Error]
    C -->|Valid| E{Check Cache}
    E -->|Cache Hit| F[Return Cached Statement]
    E -->|Cache Miss| G[Query Database]
    G --> H[Fetch Account Details]
    H --> I[Fetch Transactions in Range]
    I --> J[Calculate Opening Balance]
    J --> K[Calculate Closing Balance]
    K --> L[Calculate Totals]
    L --> M{Format Type?}
    M -->|JSON| N[Format as JSON]
    M -->|PDF| O[Generate PDF]
    M -->|CSV| P[Generate CSV]
    M -->|XLSX| Q[Generate XLSX]
    N --> R[Cache Result]
    O --> R
    P --> R
    Q --> R
    R --> S[Return Statement]
    S --> T[Log Request]
    T --> U[End]
    D --> U
    F --> U
```

## 10. Session Management Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as Frontend
    participant API as API Gateway
    participant Auth as Auth Service
    participant Redis as Session Store
    participant DB as Database
    
    Note over User,DB: User has valid access token
    
    User->>UI: Make API request
    UI->>API: Request + Access Token
    API->>Auth: Validate token
    Auth->>Auth: Decode JWT
    Auth->>Auth: Check expiration
    
    alt Token expired
        Auth-->>API: 401 Unauthorized
        API-->>UI: Token expired
        UI->>UI: Attempt token refresh
        UI->>API: POST /api/auth/refresh + Refresh Token
        API->>Auth: Validate refresh token
        Auth->>Redis: Check session
        
        alt Session invalid/expired
            Redis-->>Auth: Not found
            Auth-->>API: 401 Unauthorized
            API-->>UI: Session expired
            UI->>UI: Clear tokens
            UI-->>User: Redirect to login
        else Session valid
            Redis-->>Auth: Session found
            Auth->>Auth: Generate new access token
            Auth->>Auth: Rotate refresh token
            Auth->>Redis: Update session
            Auth->>DB: Log token refresh
            Auth-->>API: New tokens
            API-->>UI: New tokens
            UI->>UI: Store new tokens
            UI->>API: Retry original request
            API->>Auth: Validate new token
            Auth-->>API: Token valid
            API->>API: Process request
            API-->>UI: Response
            UI-->>User: Show result
        end
    else Token valid
        Auth->>Redis: Update last activity
        Auth-->>API: Token valid
        API->>API: Process request
        API-->>UI: Response
        UI-->>User: Show result
    end
```

## 11. Transaction Rollback Flow

```mermaid
sequenceDiagram
    participant Admin
    participant API as API Gateway
    participant Txn as Transaction Service
    participant DB as Database
    participant Audit as Audit Service
    participant Notif as Notification Service
    
    Admin->>API: Request rollback
    API->>Txn: POST /api/transactions/:id/rollback
    Txn->>DB: Load transaction
    Txn->>Txn: Validate rollback eligibility
    
    alt Not eligible
        Txn-->>API: 400 Bad Request
        API-->>Admin: Cannot rollback
    else Eligible
        Txn->>DB: Begin transaction
        Txn->>DB: Create reversal transaction
        Txn->>DB: Credit source account
        Txn->>DB: Debit destination account
        Txn->>DB: Update original transaction status
        Txn->>DB: Update transaction limits
        Txn->>DB: Commit transaction
        Txn->>Audit: Log rollback
        Txn->>Notif: Send notifications
        Txn-->>API: 200 OK
        API-->>Admin: Rollback successful
    end
```

## 12. System Health Check Flow

```mermaid
flowchart TD
    A[Health Check Request] --> B[Check API Gateway]
    B --> C{Gateway Healthy?}
    C -->|No| D[Return 503 Unhealthy]
    C -->|Yes| E[Check Database]
    E --> F{DB Connected?}
    F -->|No| G[Log Error]
    G --> D
    F -->|Yes| H[Check Redis]
    H --> I{Redis Connected?}
    I -->|No| J[Log Warning]
    J --> K[Degraded Mode]
    I -->|Yes| L[Check Kafka]
    L --> M{Kafka Connected?}
    M -->|No| N[Log Warning]
    N --> K
    M -->|Yes| O[Check External Services]
    O --> P{All Services OK?}
    P -->|No| Q[Log Issues]
    Q --> K
    P -->|Yes| R[Return 200 Healthy]
    K --> S[Return 200 Degraded]
    R --> T[End]
    S --> T
    D --> T
```

These flow diagrams provide a comprehensive visual representation of the key processes in the banking system, making it easier to understand the system's behavior and interactions between components.
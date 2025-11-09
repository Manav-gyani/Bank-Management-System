# System Design Document

## Overview

This document describes the architectural design of the Banking System, a scalable, secure, and highly available platform for managing financial transactions and accounts.

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ Web App  │  │ Mobile   │  │  Admin   │  │   API    │   │
│  │ (React)  │  │   iOS/   │  │  Portal  │  │  Clients │   │
│  │          │  │  Android │  │          │  │          │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        │ HTTPS/WSS
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway Layer                       │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Load Balancer (NGINX/AWS ALB)                         │ │
│  │  ├─ Rate Limiting                                      │ │
│  │  ├─ SSL Termination                                    │ │
│  │  ├─ Request Routing                                    │ │
│  │  └─ DDoS Protection                                    │ │
│  └────────────────────────────────────────────────────────┘ │
└───────────────────────┬─────────────────────────────────────┘
                        │
            ┌───────────┼───────────┐
            │           │           │
            ▼           ▼           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │   Auth   │  │ Account  │  │Transaction│ │   User   │   │
│  │ Service  │  │ Service  │  │  Service │  │ Service  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │Notify    │  │Analytics │  │  Fraud   │  │  Audit   │   │
│  │ Service  │  │ Service  │  │ Detection│  │ Service  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└───────────────────────┬─────────────────────────────────────┘
                        │
            ┌───────────┼───────────┐
            │           │           │
            ▼           ▼           ▼
┌─────────────────────────────────────────────────────────────┐
│                      Data Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│  │MongoDB   │  │  Redis   │  │MongoDB   │  │ Elastic  │     │
│  │ (Primary)│  │  (Cache) │  │ (Logs)   │  │ (Search) │     │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘     │
│  ┌──────────┐  ┌──────────┐                                 │
│  │   S3     │  │  Kafka   │                                 │
│  │(Documents│  │ (Events) │                                 │
│  └──────────┘  └──────────┘                                 │
└─────────────────────────────────────────────────────────────┘
```

## Core Components

### 1. API Gateway

**Technology:** NGINX / AWS Application Load Balancer

**Responsibilities:**
- SSL/TLS termination
- Load balancing across service instances
- Rate limiting and throttling
- Request/response transformation
- API versioning
- CORS handling
- DDoS protection

**Configuration:**
```nginx
upstream auth_service {
    least_conn;
    server auth-1:3001;
    server auth-2:3001;
    server auth-3:3001;
}

server {
    listen 443 ssl http2;
    server_name api.bankingsystem.com;
    
    ssl_certificate /etc/ssl/certs/api.crt;
    ssl_certificate_key /etc/ssl/private/api.key;
    
    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=100r/s;
    limit_req zone=api_limit burst=20 nodelay;
    
    location /api/auth {
        proxy_pass http://auth_service;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 2. Microservices

#### Authentication Service
- User registration and login
- JWT token generation and validation
- 2FA management
- Session management
- Password reset flows

#### Account Service
- Account creation and management
- Balance inquiries
- Account statements
- Interest calculations
- Account closure

#### Transaction Service
- Fund transfers (internal/external)
- Transaction processing
- Transaction history
- Recurring payments
- Transaction limits enforcement

#### Notification Service
- Email notifications
- SMS alerts
- Push notifications
- In-app notifications
- Notification preferences

#### Fraud Detection Service
- Real-time transaction monitoring
- Anomaly detection
- Risk scoring
- Velocity checks
- Geo-location verification

#### Analytics Service
- Transaction analytics
- User behavior analysis
- Reporting and dashboards
- Data aggregation
- Business intelligence

### 3. Data Storage

#### Primary Database - PostgreSQL

**Schema Design:**
- Users, Accounts, Transactions tables
- ACID compliance for financial data
- Master-slave replication
- Connection pooling

**Optimization:**
- Indexed columns for fast lookups
- Partitioning for large tables
- Query optimization
- Regular VACUUM operations

#### Cache Layer - Redis

**Use Cases:**
- Session storage
- Token blacklist
- Rate limiting counters
- Frequently accessed data
- Real-time analytics

**Configuration:**
```redis
# Redis Cluster Configuration
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
maxmemory 2gb
maxmemory-policy allkeys-lru
```

#### Document Store - MongoDB

**Use Cases:**
- Audit logs
- Application logs
- User activity tracking
- Unstructured data

#### Search Engine - Elasticsearch

**Use Cases:**
- Transaction search
- Full-text search
- Log analysis
- Real-time indexing

## Communication Patterns

### 1. Synchronous Communication

**REST APIs:**
- Request-response pattern
- HTTP/HTTPS protocol
- JSON payload format
- Stateless operations

**gRPC:**
- High-performance RPC
- Protocol Buffers
- Bi-directional streaming
- Service-to-service communication

### 2. Asynchronous Communication

**Message Queue - Apache Kafka:**
```
Topics:
├── transactions.created
├── transactions.completed
├── accounts.created
├── notifications.email
├── notifications.sms
├── fraud.alerts
└── audit.events
```

**Event-Driven Architecture:**
- Event producers publish to Kafka
- Event consumers subscribe to topics
- Guaranteed delivery
- Event replay capability

## Security Architecture

### 1. Authentication & Authorization

**JWT Token Structure:**
```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT"
  },
  "payload": {
    "userId": "usr_123abc",
    "email": "user@example.com",
    "roles": ["user"],
    "sessionId": "sess_xyz789",
    "iat": 1699536000,
    "exp": 1699536900
  }
}
```

**Role-Based Access Control (RBAC):**
```
Roles:
├── USER
│   ├── view_own_accounts
│   ├── create_transaction
│   └── update_profile
├── ADMIN
│   ├── view_all_accounts
│   ├── freeze_accounts
│   └── access_audit_logs
└── SUPER_ADMIN
    ├── all_admin_permissions
    └── system_configuration
```

### 2. Data Encryption

**At Rest:**
- AES-256 encryption for sensitive data
- Encrypted database backups
- Encrypted file storage (S3)
- Hardware Security Modules (HSM) for keys

**In Transit:**
- TLS 1.3 for all communications
- Certificate pinning for mobile apps
- VPN for internal services
- End-to-end encryption for sensitive operations

### 3. Security Layers

```
┌────────────────────────────────────────┐
│     WAF (Web Application Firewall)     │
├────────────────────────────────────────┤
│     DDoS Protection (Cloudflare)       │
├────────────────────────────────────────┤
│     API Gateway Rate Limiting          │
├────────────────────────────────────────┤
│     Application Authentication         │
├────────────────────────────────────────┤
│     Service-to-Service mTLS           │
├────────────────────────────────────────┤
│     Database Access Controls           │
└────────────────────────────────────────┘
```

## Scalability Design

### 1. Horizontal Scaling

**Service Replication:**
- Multiple instances of each microservice
- Auto-scaling based on metrics
- Load balancing across instances
- Stateless service design

**Database Scaling:**
- Read replicas for read-heavy operations
- Sharding for write-heavy operations
- Connection pooling
- Query optimization

### 2. Caching Strategy

**Multi-Level Caching:**
```
┌─────────────────────────────────────────┐
│         Browser Cache (5 minutes)       │
├─────────────────────────────────────────┤
│         CDN Cache (1 hour)             │
├─────────────────────────────────────────┤
│    Application Cache (15 minutes)      │
├─────────────────────────────────────────┤
│         Redis Cache (30 minutes)       │
├─────────────────────────────────────────┤
│           Database                      │
└─────────────────────────────────────────┘
```

**Cache Invalidation:**
- Time-based expiration
- Event-based invalidation
- Write-through caching
- Cache warming on deployment

### 3. Load Balancing

**Strategies:**
- Round Robin for even distribution
- Least Connections for optimal resource usage
- IP Hash for session affinity
- Weighted distribution for gradual rollouts

## High Availability

### 1. Redundancy

**Multi-AZ Deployment:**
```
Region: us-east-1
├── Availability Zone A
│   ├── App Servers (3 instances)
│   ├── Database Primary
│   └── Redis Master
├── Availability Zone B
│   ├── App Servers (3 instances)
│   ├── Database Replica
│   └── Redis Replica
└── Availability Zone C
    ├── App Servers (2 instances)
    ├── Database Replica
    └── Redis Replica
```

### 2. Disaster Recovery

**Backup Strategy:**
- Automated daily backups
- Point-in-time recovery
- Cross-region replication
- Backup retention: 30 days

**RTO (Recovery Time Objective):** 1 hour
**RPO (Recovery Point Objective):** 5 minutes

### 3. Health Monitoring

**Health Checks:**
```javascript
// Service health endpoint
app.get('/health', async (req, res) => {
  const health = {
    uptime: process.uptime(),
    status: 'healthy',
    timestamp: Date.now(),
    checks: {
      database: await checkDatabase(),
      redis: await checkRedis(),
      kafka: await checkKafka()
    }
  };
  
  const isHealthy = Object.values(health.checks)
    .every(check => check.status === 'up');
  
  res.status(isHealthy ? 200 : 503).json(health);
});
```

## Monitoring & Observability

### 1. Metrics Collection

**Prometheus Metrics:**
- Request rate and latency
- Error rates
- Database connection pool
- Cache hit/miss ratio
- Queue depth

### 2. Logging

**Structured Logging:**
```json
{
  "timestamp": "2025-11-09T10:30:00Z",
  "level": "INFO",
  "service": "transaction-service",
  "traceId": "abc123xyz",
  "userId": "usr_123abc",
  "action": "transfer",
  "amount": 500.00,
  "status": "success",
  "duration": 245
}
```

**Log Aggregation:**
- Elasticsearch for storage
- Logstash for processing
- Kibana for visualization
- Retention: 90 days

### 3. Distributed Tracing

**OpenTelemetry:**
- Request tracing across services
- Performance bottleneck identification
- Dependency visualization
- Error tracking

### 4. Alerting

**Alert Rules:**
```yaml
alerts:
  - name: HighErrorRate
    condition: error_rate > 1%
    duration: 5m
    severity: critical
    
  - name: DatabaseConnectionPool
    condition: pool_usage > 80%
    duration: 5m
    severity: warning
    
  - name: ResponseTimeHigh
    condition: p95_latency > 1000ms
    duration: 10m
    severity: warning
```

## Performance Optimization

### 1. Database Optimization

**Indexing Strategy:**
```sql
-- Composite index for frequent queries
CREATE INDEX idx_transactions_account_date 
ON transactions(account_id, created_at DESC);

-- Partial index for active accounts
CREATE INDEX idx_active_accounts 
ON accounts(user_id) WHERE status = 'ACTIVE';
```

**Query Optimization:**
- Use EXPLAIN ANALYZE for query planning
- Avoid N+1 queries
- Batch operations where possible
- Use prepared statements

### 2. API Optimization

**Response Compression:**
- Gzip compression for responses > 1KB
- Reduces bandwidth by 70-80%

**Pagination:**
- Cursor-based for large datasets
- Limit max page size to 100
- Include total count only when requested

**Field Selection:**
- Allow clients to specify fields
- Reduce unnecessary data transfer
- GraphQL for flexible queries

### 3. Caching Strategies

**Cache-Aside Pattern:**
```javascript
async function getAccount(accountId) {
  // Try cache first
  let account = await cache.get(`account:${accountId}`);
  
  if (!account) {
    // Cache miss - fetch from database
    account = await db.accounts.findById(accountId);
    
    // Store in cache
    await cache.set(`account:${accountId}`, account, 900); // 15 min TTL
  }
  
  return account;
}
```

## Capacity Planning

### Current Capacity

| Metric | Value |
|--------|-------|
| Daily Active Users | 10,000 |
| Transactions per Day | 50,000 |
| Peak TPS | 50 |
| Average Response Time | 200ms |
| Database Size | 100GB |

### Scaling Thresholds

| Resource | Current | Threshold | Action |
|----------|---------|-----------|--------|
| CPU Usage | 45% | 70% | Add instances |
| Memory Usage | 60% | 80% | Add instances |
| Database Connections | 100 | 150 | Increase pool size |
| Queue Depth | 1000 | 5000 | Add consumers |

## Technology Stack

### Backend
- **Runtime:** Node.js 20.x
- **Framework:** Express.js 4.x
- **Language:** TypeScript 5.x
- **API Documentation:** OpenAPI 3.0

### Databases
- **Primary DB:** PostgreSQL 15
- **Cache:** Redis 7.x
- **Document Store:** MongoDB 6.x
- **Search:** Elasticsearch 8.x

### Message Queue
- **Event Streaming:** Apache Kafka 3.x
- **Task Queue:** Bull (Redis-based)

### DevOps
- **Containerization:** Docker
- **Orchestration:** Kubernetes
- **CI/CD:** GitHub Actions / Jenkins
- **Infrastructure:** Terraform
- **Cloud Provider:** AWS / GCP

### Monitoring
- **Metrics:** Prometheus + Grafana
- **Logging:** ELK Stack
- **Tracing:** Jaeger / OpenTelemetry
- **APM:** New Relic / Datadog

## Deployment Strategy

### Blue-Green Deployment

```
┌─────────────────────────────────────┐
│      Load Balancer                  │
└───────────┬─────────────────────────┘
            │
      ┌─────┴─────┐
      ▼           ▼
┌──────────┐  ┌──────────┐
│  Blue    │  │  Green   │
│(Current) │  │  (New)   │
│ v1.2.0   │  │ v1.3.0   │
└──────────┘  └──────────┘
```

**Process:**
1. Deploy new version to Green environment
2. Run smoke tests
3. Gradually shift traffic (10% → 50% → 100%)
4. Monitor metrics
5. Rollback if issues detected
6. Blue becomes standby after successful deployment

## Conclusion

This system design provides a robust, scalable, and secure foundation for the banking system. It emphasizes high availability, performance, and security while maintaining flexibility for future enhancements and scaling.
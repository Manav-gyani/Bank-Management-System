# Authentication Guide

## Overview

The banking system uses JWT (JSON Web Tokens) for authentication with a two-token approach: access tokens and refresh tokens.

## Authentication Flow

### 1. User Registration

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "dateOfBirth": "1990-01-15",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "user": {
      "id": "usr_123abc",
      "username": "johndoe",
      "email": "john@example.com",
      "status": "PENDING_VERIFICATION"
    },
    "tokens": {
      "accessToken": "eyJhbGc...",
      "refreshToken": "eyJhbGc...",
      "expiresIn": 900
    }
  }
}
```

### 2. User Login

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_123abc",
      "username": "johndoe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "status": "ACTIVE"
    },
    "tokens": {
      "accessToken": "eyJhbGc...",
      "refreshToken": "eyJhbGc...",
      "expiresIn": 900
    }
  }
}
```

### 3. Token Refresh

**Endpoint:** `POST /api/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGc..."
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "expiresIn": 900
  }
}
```

### 4. Logout

**Endpoint:** `POST /api/auth/logout`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response:**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

## Token Management

### Access Tokens
- **Lifetime:** 15 minutes (900 seconds)
- **Storage:** Memory or sessionStorage (never localStorage)
- **Usage:** Included in Authorization header for all protected endpoints
- **Format:** `Authorization: Bearer {accessToken}`

### Refresh Tokens
- **Lifetime:** 7 days
- **Storage:** HttpOnly cookie (recommended) or secure storage
- **Usage:** Only for obtaining new access tokens
- **Rotation:** New refresh token issued with each refresh request

## Security Best Practices

### Password Requirements
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character
- Cannot contain username or email

### Rate Limiting
- Login attempts: 5 per 15 minutes per IP
- Registration: 3 per hour per IP
- Token refresh: 10 per hour per user
- Password reset: 3 per hour per email

### Account Lockout
- Account locked after 5 failed login attempts
- Lockout duration: 30 minutes
- Admin can manually unlock accounts
- User receives email notification on lockout

## Two-Factor Authentication (2FA)

### Enable 2FA

**Endpoint:** `POST /api/auth/2fa/enable`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "qrCode": "data:image/png;base64,iVBORw0KG...",
    "secret": "JBSWY3DPEHPK3PXP",
    "backupCodes": [
      "12345678",
      "87654321",
      "11223344"
    ]
  }
}
```

### Verify 2FA Setup

**Endpoint:** `POST /api/auth/2fa/verify`

**Request Body:**
```json
{
  "token": "123456"
}
```

### Login with 2FA

**Endpoint:** `POST /api/auth/login/2fa`

**Request Body:**
```json
{
  "sessionId": "sess_abc123",
  "token": "123456"
}
```

### Disable 2FA

**Endpoint:** `POST /api/auth/2fa/disable`

**Request Body:**
```json
{
  "password": "SecurePass123!",
  "token": "123456"
}
```

## Password Management

### Change Password

**Endpoint:** `POST /api/auth/password/change`

**Request Body:**
```json
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass123!",
  "confirmPassword": "NewPass123!"
}
```

### Forgot Password

**Endpoint:** `POST /api/auth/password/forgot`

**Request Body:**
```json
{
  "email": "john@example.com"
}
```

### Reset Password

**Endpoint:** `POST /api/auth/password/reset`

**Request Body:**
```json
{
  "token": "reset_token_abc123",
  "newPassword": "NewPass123!",
  "confirmPassword": "NewPass123!"
}
```

## Session Management

### Get Active Sessions

**Endpoint:** `GET /api/auth/sessions`

**Response:**
```json
{
  "success": true,
  "data": {
    "sessions": [
      {
        "id": "sess_123",
        "device": "Chrome on Windows",
        "ipAddress": "192.168.1.1",
        "location": "New York, US",
        "lastActive": "2025-11-09T10:30:00Z",
        "current": true
      }
    ]
  }
}
```

### Revoke Session

**Endpoint:** `DELETE /api/auth/sessions/:sessionId`

### Revoke All Sessions

**Endpoint:** `DELETE /api/auth/sessions/all`

## Email Verification

### Send Verification Email

**Endpoint:** `POST /api/auth/email/verify/send`

### Verify Email

**Endpoint:** `GET /api/auth/email/verify/:token`

## Error Codes

| Code | Message | Description |
|------|---------|-------------|
| AUTH001 | Invalid credentials | Username or password incorrect |
| AUTH002 | Account locked | Too many failed login attempts |
| AUTH003 | Account not verified | Email verification required |
| AUTH004 | Invalid token | Token expired or invalid |
| AUTH005 | Token expired | Access token has expired |
| AUTH006 | Invalid refresh token | Refresh token invalid or revoked |
| AUTH007 | 2FA required | Two-factor authentication needed |
| AUTH008 | Invalid 2FA code | 2FA token incorrect |
| AUTH009 | Session expired | User session has expired |
| AUTH010 | Unauthorized | Authentication required |

## Security Headers

All authentication endpoints return the following security headers:

```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
```

## Implementation Example

### Frontend (React)

```javascript
// auth.service.js
import axios from 'axios';

class AuthService {
  async login(username, password) {
    const response = await axios.post('/api/auth/login', {
      username,
      password
    });
    
    this.setTokens(response.data.tokens);
    return response.data;
  }

  async refreshToken() {
    const refreshToken = this.getRefreshToken();
    const response = await axios.post('/api/auth/refresh', {
      refreshToken
    });
    
    this.setAccessToken(response.data.accessToken);
    return response.data;
  }

  setTokens({ accessToken, refreshToken }) {
    sessionStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  getAccessToken() {
    return sessionStorage.getItem('accessToken');
  }

  logout() {
    sessionStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }
}

// axios interceptor
axios.interceptors.request.use(
  (config) => {
    const token = authService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        await authService.refreshToken();
        return axios(originalRequest);
      } catch (refreshError) {
        authService.logout();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);
```

### Backend (Node.js)

```javascript
// auth.middleware.js
const jwt = require('jsonwebtoken');

const authenticate = async (req, res, next) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    
    if (!token) {
      return res.status(401).json({
        success: false,
        error: { code: 'AUTH010', message: 'Authentication required' }
      });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.userId = decoded.userId;
    req.sessionId = decoded.sessionId;
    
    next();
  } catch (error) {
    if (error.name === 'TokenExpiredError') {
      return res.status(401).json({
        success: false,
        error: { code: 'AUTH005', message: 'Token expired' }
      });
    }
    
    return res.status(401).json({
      success: false,
      error: { code: 'AUTH004', message: 'Invalid token' }
    });
  }
};

module.exports = { authenticate };
```

## Best Practices

1. **Never store tokens in localStorage** - Use sessionStorage or memory for access tokens
2. **Implement token rotation** - Issue new refresh tokens with each refresh
3. **Use HTTPS only** - All authentication endpoints must use HTTPS
4. **Implement CSRF protection** - Use CSRF tokens for state-changing operations
5. **Log security events** - Track failed logins, password changes, etc.
6. **Use strong password hashing** - bcrypt with cost factor of 12 or higher
7. **Implement rate limiting** - Prevent brute force attacks
8. **Add device fingerprinting** - Track unusual login patterns
9. **Send security notifications** - Alert users of suspicious activity
10. **Regular security audits** - Review and update security measures
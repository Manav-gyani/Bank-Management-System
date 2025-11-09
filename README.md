# Bank Management System

A comprehensive bank management system built with Java Spring Boot, MongoDB, and React.

## 🚀 Features

- **User Authentication & Authorization** - JWT-based authentication with role-based access control
- **Account Management** - Create and manage multiple account types (Savings, Current, FD, RD)
- **Transactions** - Deposit, Withdraw, and Transfer functionality
- **Transaction History** - View detailed transaction history with filters
- **Customer Management** - Complete customer profile management
- **Dashboard** - Overview of accounts, balances, and recent transactions
- **Responsive Design** - Works seamlessly on desktop and mobile devices

## 🛠️ Technology Stack

### Backend
- **Java 23**
- **Spring Boot 2.7.x**
- **Spring Security** (JWT Authentication)
- **Spring Data MongoDB**
- **MongoDB 6.0**
- **Maven**

### Frontend
- **React 18**
- **Redux Toolkit** (State Management)
- **React Router v6** (Routing)
- **Axios** (HTTP Client)
- **Tailwind CSS** (Styling)
- **Lucide React** (Icons)

## 📋 Prerequisites

- Java 23 
- Node.js 16 or higher
- MongoDB 6.0 or higher
- Maven 3.6 or higher
- Docker & Docker Compose (optional)

## 🔧 Installation

### Using Docker (Recommended)

1. Clone the repository
```bash
git clone https://github.com/yourusername/bank-management-system.git
cd bank-management-system
```

2. Start all services using Docker Compose
```bash
docker-compose up -d
```

3. Access the application
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- MongoDB: mongodb://localhost:27017

### Manual Installation

#### Backend Setup

1. Navigate to backend directory
```bash
cd backend
```

2. Configure MongoDB connection in `src/main/resources/application.properties`
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/bankdb
```

3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

The backend will start on http://localhost:8080

#### Frontend Setup

1. Navigate to frontend directory
```bash
cd frontend
```

2. Install dependencies
```bash
npm install
```

3. Create `.env` file
```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

4. Start development server
```bash
npm start
```

The frontend will start on http://localhost:3000

## 📁 Project Structure

```
bank-management-system/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bank/
│   │   │   │   ├── config/    # Configuration classes
│   │   │   │   ├── controller/ # REST controllers
│   │   │   │   ├── model/     # Domain models
│   │   │   │   ├── repository/ # MongoDB repositories
│   │   │   │   ├── service/   # Business logic
│   │   │   │   ├── security/  # JWT security
│   │   │   │   └── exception/ # Exception handling
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
├── frontend/                   # React frontend
│   ├── src/
│   │   ├── components/        # Reusable components
│   │   ├── pages/            # Page components
│   │   ├── services/         # API services
│   │   ├── store/            # Redux store
│   │   └── App.js
│   └── package.json
│
├── database/                  # Database scripts
│   └── mongodb/
│       ├── init-db.js
│       └── seed-data.js
│
└── docker-compose.yml
```

## 🔑 Default Credentials

**Admin Account:**
- Username: `admin`
- Password: `admin123`

**Demo User:**
- Username: `abctest`
- Password: `password123`

## 📚 API Documentation

### Authentication Endpoints

```
POST /api/auth/login          - User login
POST /api/auth/register       - User registration
GET  /api/auth/check-username - Check username availability
GET  /api/auth/check-email    - Check email availability
```

### Account Endpoints

```
POST   /api/accounts                    - Create new account
GET    /api/accounts/{accountNumber}    - Get account details
GET    /api/accounts/customer/{customerId} - Get customer accounts
GET    /api/accounts/{accountNumber}/balance - Get account balance
POST   /api/accounts/{accountNumber}/deposit - Deposit money
POST   /api/accounts/{accountNumber}/withdraw - Withdraw money
POST   /api/accounts/transfer              - Transfer money
```

### Transaction Endpoints

```
GET /api/transactions/account/{accountId}           - Get account transactions
GET /api/transactions/account/{accountId}/date-range - Get transactions by date
GET /api/transactions/{id}                          - Get transaction by ID
GET /api/transactions                               - Get all transactions (Admin)
```

### Customer Endpoints

```
POST   /api/customers     - Create customer
GET    /api/customers/{id} - Get customer
GET    /api/customers      - Get all customers
PUT    /api/customers/{id} - Update customer
DELETE /api/customers/{id} - Delete customer
```

## 🚢 Deployment

### Docker Deployment

Build and deploy using Docker:
```bash
docker-compose up --build -d
```

### Production Build

**Backend:**
```bash
cd backend
mvn clean package
java -jar target/bank-management-system-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd frontend
npm run build
# Serve the build folder using a web server
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👥 Authors

- Manav Gyani
- Kavya Mankodi

## 🙏 Acknowledgments

- Spring Boot Documentation
- React Documentation
- MongoDB Documentation
- Tailwind CSS
- Lucide Icons
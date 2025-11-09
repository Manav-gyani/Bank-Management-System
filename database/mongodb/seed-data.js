db = db.getSiblingDB('bankdb');

// Seed admin user
db.users.insertOne({
  username: 'admin',
  email: 'admin@bank.com',
  password: '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', // password: admin123
  roles: ['ADMIN', 'EMPLOYEE'],
  enabled: true,
  createdAt: new Date(),
  updatedAt: new Date()
});

// Seed demo customer
const customerId = ObjectId();
db.customers.insertOne({
  _id: customerId,
  firstName: 'John',
  lastName: 'Doe',
  email: 'john.doe@example.com',
  phone: '+919876543210',
  address: '123 Main Street, Vadodara, Gujarat, India',
  aadharNumber: '1234-5678-9012',
  panNumber: 'ABCDE1234F',
  createdAt: new Date(),
  updatedAt: new Date()
});

db.users.insertOne({
  username: 'johndoe',
  email: 'john.doe@example.com',
  password: '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', // password: admin123
  roles: ['CUSTOMER'],
  enabled: true,
  customerId: customerId.toString(),
  createdAt: new Date(),
  updatedAt: new Date()
});

// Seed demo accounts
const account1Id = ObjectId();
const account2Id = ObjectId();

db.accounts.insertMany([
  {
    _id: account1Id,
    accountNumber: '1000000001',
    customerId: customerId.toString(),
    accountType: 'SAVINGS',
    balance: NumberDecimal('45000.50'),
    currency: 'INR',
    status: 'ACTIVE',
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: account2Id,
    accountNumber: '2000000001',
    customerId: customerId.toString(),
    accountType: 'CURRENT',
    balance: NumberDecimal('120000.00'),
    currency: 'INR',
    status: 'ACTIVE',
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

// Seed demo transactions
db.transactions.insertMany([
  {
    transactionId: 'TXN' + Date.now() + '001',
    accountId: account1Id.toString(),
    type: 'DEPOSIT',
    amount: NumberDecimal('45000.50'),
    balanceAfter: NumberDecimal('45000.50'),
    description: 'Initial Deposit',
    referenceNumber: 'REF001',
    status: 'COMPLETED',
    timestamp: new Date('2025-10-01T10:00:00Z')
  },
  {
    transactionId: 'TXN' + Date.now() + '002',
    accountId: account2Id.toString(),
    type: 'DEPOSIT',
    amount: NumberDecimal('120000.00'),
    balanceAfter: NumberDecimal('120000.00'),
    description: 'Initial Deposit',
    referenceNumber: 'REF002',
    status: 'COMPLETED',
    timestamp: new Date('2025-10-01T10:05:00Z')
  },
  {
    transactionId: 'TXN' + Date.now() + '003',
    accountId: account1Id.toString(),
    type: 'WITHDRAWAL',
    amount: NumberDecimal('1000.00'),
    balanceAfter: NumberDecimal('44000.50'),
    description: 'ATM Withdrawal',
    referenceNumber: 'REF003',
    status: 'COMPLETED',
    timestamp: new Date('2025-10-15T14:30:00Z')
  },
  {
    transactionId: 'TXN' + Date.now() + '004',
    accountId: account1Id.toString(),
    type: 'DEPOSIT',
    amount: NumberDecimal('1000.00'),
    balanceAfter: NumberDecimal('45000.50'),
    description: 'Salary Credit',
    referenceNumber: 'REF004',
    status: 'COMPLETED',
    timestamp: new Date('2025-10-25T09:00:00Z')
  }
]);

print('Demo data seeded successfully');

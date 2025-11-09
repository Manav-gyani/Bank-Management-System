db = db.getSiblingDB('bankdb');

// Create collections
db.createCollection('users');
db.createCollection('customers');
db.createCollection('accounts');
db.createCollection('transactions');
db.createCollection('beneficiaries');
db.createCollection('cards');
db.createCollection('loans');

// Create indexes for better performance
db.users.createIndex({ username: 1 }, { unique: true });
db.users.createIndex({ email: 1 }, { unique: true });

db.customers.createIndex({ email: 1 }, { unique: true });
db.customers.createIndex({ aadharNumber: 1 }, { unique: true });
db.customers.createIndex({ panNumber: 1 }, { unique: true });

db.accounts.createIndex({ accountNumber: 1 }, { unique: true });
db.accounts.createIndex({ customerId: 1 });
db.accounts.createIndex({ status: 1 });

db.transactions.createIndex({ accountId: 1 });
db.transactions.createIndex({ transactionId: 1 });
db.transactions.createIndex({ timestamp: -1 });

db.beneficiaries.createIndex({ customerId: 1 });
db.beneficiaries.createIndex({ accountNumber: 1 });

print('Database initialized successfully');

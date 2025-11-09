db = db.getSiblingDB('bankdb');

// Additional indexes for optimization
db.transactions.createIndex({ accountId: 1, timestamp: -1 });
db.transactions.createIndex({ type: 1, status: 1 });

db.accounts.createIndex({ customerId: 1, accountType: 1 });
db.accounts.createIndex({ status: 1, accountType: 1 });

db.customers.createIndex({ createdAt: -1 });

print('Additional indexes created successfully');
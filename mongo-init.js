// MongoDB initialization script
db = db.getSiblingDB('budgeter');

// Create collections
db.createCollection('expenses');

// Create indexes for better performance
db.expenses.createIndex({ "category": 1 });
db.expenses.createIndex({ "createdAt": 1 });
db.expenses.createIndex({ "tags": 1 });

print('Budgeter database initialized successfully!');

-- Migration: Insert initial data
-- Version: 4
-- Description: Insert sample users and payments for testing

-- Set schema context to temp
SET search_path TO temp;

-- Insert sample users
INSERT INTO users (username, email, first_name, last_name, status) 
VALUES 
    ('john_doe', 'john.doe@example.com', 'John', 'Doe', 'ACTIVE'),
    ('jane_smith', 'jane.smith@example.com', 'Jane', 'Smith', 'ACTIVE'),
    ('bob_wilson', 'bob.wilson@example.com', 'Bob', 'Wilson', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- Insert sample payments (assuming users exist)
INSERT INTO payments (user_id, amount, currency, status, payment_method, transaction_id)
SELECT 
    u.id,
    100.00,
    'USD',
    'COMPLETED',
    'CREDIT_CARD',
    'TXN-' || u.id || '-001'
FROM temp.users u
WHERE u.username = 'john_doe'
ON CONFLICT (transaction_id) DO NOTHING;

INSERT INTO payments (user_id, amount, currency, status, payment_method, transaction_id)
SELECT 
    u.id,
    250.50,
    'USD',
    'COMPLETED',
    'DEBIT_CARD',
    'TXN-' || u.id || '-002'
FROM temp.users u
WHERE u.username = 'jane_smith'
ON CONFLICT (transaction_id) DO NOTHING;

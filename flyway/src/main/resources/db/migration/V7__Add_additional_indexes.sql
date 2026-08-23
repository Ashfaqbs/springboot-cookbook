-- Migration: Add additional indexes for better query performance
-- Version: 7
-- Description: Add indexes on commonly queried columns in users and payments tables

-- Set schema context to temp
SET search_path TO temp;

-- ============================================
-- Indexes for users table
-- ============================================

-- Index on username for faster lookups (even though it's unique, explicit index helps)
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Composite index on first_name and last_name for name-based searches
CREATE INDEX IF NOT EXISTS idx_users_name ON users(first_name, last_name);

-- Index on created_at for date range queries and sorting
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Index on updated_at for tracking recent updates
CREATE INDEX IF NOT EXISTS idx_users_updated_at ON users(updated_at);

-- Composite index on status and created_at for filtering active users by date
CREATE INDEX IF NOT EXISTS idx_users_status_created ON users(status, created_at);

-- ============================================
-- Indexes for payments table
-- ============================================

-- Index on payment_method for filtering by payment method type
CREATE INDEX IF NOT EXISTS idx_payments_payment_method ON payments(payment_method);

-- Index on currency for filtering by currency type
CREATE INDEX IF NOT EXISTS idx_payments_currency ON payments(currency);

-- Index on amount for range queries (e.g., payments between $100 and $500)
CREATE INDEX IF NOT EXISTS idx_payments_amount ON payments(amount);

-- Index on updated_at for tracking payment updates
CREATE INDEX IF NOT EXISTS idx_payments_updated_at ON payments(updated_at);

-- Composite index on status and amount for filtering payments by status and amount range
CREATE INDEX IF NOT EXISTS idx_payments_status_amount ON payments(status, amount);

-- Composite index on user_id and status for finding user's payments by status
CREATE INDEX IF NOT EXISTS idx_payments_user_status ON payments(user_id, status);

-- Composite index on created_at and status for date range queries with status filter
CREATE INDEX IF NOT EXISTS idx_payments_created_status ON payments(created_at, status);

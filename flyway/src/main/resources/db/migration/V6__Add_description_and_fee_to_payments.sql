-- Migration: Add description and fee_amount columns to payments table
-- Version: 6
-- Description: Add description for payment details and fee_amount to track transaction fees

-- Set schema context to temp
SET search_path TO temp;

-- Add description column to payments table
ALTER TABLE payments 
ADD COLUMN IF NOT EXISTS description VARCHAR(500);

-- Add fee_amount column to payments table
ALTER TABLE payments 
ADD COLUMN IF NOT EXISTS fee_amount DECIMAL(10, 2) DEFAULT 0.00;

-- Create index on description for search functionality (optional, only if needed for searches)
-- CREATE INDEX IF NOT EXISTS idx_payments_description ON payments(description);

-- Add comments to columns
COMMENT ON COLUMN temp.payments.description IS 'Payment description or notes';
COMMENT ON COLUMN temp.payments.fee_amount IS 'Transaction fee amount charged';

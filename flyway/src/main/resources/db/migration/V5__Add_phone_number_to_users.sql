-- Migration: Add phone number column to users table
-- Version: 5
-- Description: Add phone_number column to store user contact information

-- Set schema context to temp
SET search_path TO temp;

-- Add phone_number column to users table
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20);

-- Create index on phone_number for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_phone_number ON users(phone_number);

-- Add comment to column
COMMENT ON COLUMN temp.users.phone_number IS 'User contact phone number';

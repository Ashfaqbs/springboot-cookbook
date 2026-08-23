-- Migration: Add status column to users table
-- Version: 3
-- Description: Add status column to track user account status

-- Set schema context to temp
SET search_path TO temp;

ALTER TABLE users 
ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Create index on status for filtering
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- Add comment to column
COMMENT ON COLUMN temp.users.status IS 'User account status: ACTIVE, INACTIVE, SUSPENDED';

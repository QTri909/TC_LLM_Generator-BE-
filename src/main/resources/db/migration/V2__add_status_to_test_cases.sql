-- Add status column to test_cases table
-- Values: PENDING, PASSED, FAILED, SKIPPED
ALTER TABLE test_cases ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

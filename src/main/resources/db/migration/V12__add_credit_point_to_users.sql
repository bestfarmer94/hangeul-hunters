-- Add credit_point column to users table
ALTER TABLE users 
ADD COLUMN credit_point INT NOT NULL DEFAULT 0;

-- Set default 250 points for existing users
UPDATE users SET credit_point = 250 WHERE credit_point = 0;

-- Add index for performance
CREATE INDEX idx_users_credit_point ON users(credit_point);

-- Add device_id column to users table for guest login support
ALTER TABLE users ADD COLUMN device_id VARCHAR(255);

-- Add index for device_id to improve query performance
CREATE INDEX idx_users_device_id ON users(device_id);

-- Add lastActivityAt column to conversation table
ALTER TABLE conversation ADD COLUMN last_activity_at TIMESTAMPTZ;

-- Initialize lastActivityAt with createdAt for existing records
UPDATE conversation SET last_activity_at = created_at WHERE last_activity_at IS NULL;

-- Make lastActivityAt column NOT NULL after initializing data
ALTER TABLE conversation ALTER COLUMN last_activity_at SET NOT NULL;

-- Add index for lastActivityAt for efficient sorting
CREATE INDEX idx_conversation_last_activity_at ON conversation(last_activity_at);
CREATE INDEX idx_conversation_user_id_last_activity_at ON conversation(user_id, last_activity_at);
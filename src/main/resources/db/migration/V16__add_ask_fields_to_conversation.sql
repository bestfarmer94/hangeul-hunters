-- Add ASK-specific fields to conversation table
ALTER TABLE conversation ADD COLUMN ask_target VARCHAR(255);
ALTER TABLE conversation ADD COLUMN ask_target_closeness VARCHAR(255);

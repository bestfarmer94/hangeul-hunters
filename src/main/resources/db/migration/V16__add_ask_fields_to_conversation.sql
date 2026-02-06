-- Add ASK-specific fields to conversation table
ALTER TABLE conversation ADD COLUMN closeness VARCHAR(100) default 'Professional';
ALTER TABLE conversation ADD COLUMN ask_target VARCHAR(100);

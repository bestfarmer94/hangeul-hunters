-- Make appropriate_expression nullable in message_feedback table
ALTER TABLE message_feedback ALTER COLUMN appropriate_expression DROP NOT NULL;

COMMENT ON COLUMN message_feedback.appropriate_expression IS '적절한 표현 (nullable)';

-- Add key_expressions column to conversation_feedback table
ALTER TABLE conversation_feedback ADD COLUMN key_expressions JSONB;

COMMENT ON COLUMN conversation_feedback.key_expressions IS '핵심 표현 목록 (JSON 배열: [{korean, english, usage}, ...])';

-- Add new fields to message table
ALTER TABLE message ADD COLUMN hidden_meaning TEXT;
ALTER TABLE message ADD COLUMN visual_action TEXT;
ALTER TABLE message ADD COLUMN situation_description TEXT;
ALTER TABLE message ADD COLUMN situation_context TEXT;

COMMENT ON COLUMN message.hidden_meaning IS '숨은 의미';
COMMENT ON COLUMN message.visual_action IS '행동 묘사';
COMMENT ON COLUMN message.situation_description IS '현재 장면 묘사 (시스템 메시지)';
COMMENT ON COLUMN message.situation_context IS '전체 장면 묘사 (시스템 메시지)';

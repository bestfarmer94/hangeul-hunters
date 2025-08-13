-- 모든 테이블에 auditing 컬럼 추가

-- Users 테이블
ALTER TABLE users
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN deleted_by BIGINT;

-- AI Persona 테이블
ALTER TABLE ai_persona
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN deleted_by BIGINT;

-- Interest 테이블
ALTER TABLE interest
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN deleted_by BIGINT;

-- Conversation 테이블
ALTER TABLE conversation
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN deleted_by BIGINT;

-- Message 테이블
ALTER TABLE message
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN deleted_by BIGINT;

-- Feedback 테이블
ALTER TABLE feedback
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN deleted_by BIGINT;

-- 인덱스 추가
CREATE INDEX idx_message_created_by_type ON message(created_by, type);
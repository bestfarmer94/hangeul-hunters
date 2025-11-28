-- Add conversation type support and interview-specific fields to conversation table
ALTER TABLE conversation ADD COLUMN conversation_type VARCHAR(50) NOT NULL DEFAULT 'ROLE_PLAYING';
ALTER TABLE conversation ADD COLUMN interview_company_name VARCHAR(255);
ALTER TABLE conversation ADD COLUMN interview_job_title VARCHAR(255);
ALTER TABLE conversation ADD COLUMN interview_job_posting TEXT;
ALTER TABLE conversation ADD COLUMN interview_style VARCHAR(50);

-- Create generic file table (not interview-specific, can be used for any purpose)
CREATE TABLE file (
    id BIGSERIAL PRIMARY KEY,
    object_type VARCHAR(100) NOT NULL,
    object_id BIGINT NOT NULL,
    file_url VARCHAR(1024) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

-- Add index for conversation_id to improve query performance
CREATE INDEX idx_file_conversation_id ON file(object_type, object_id);
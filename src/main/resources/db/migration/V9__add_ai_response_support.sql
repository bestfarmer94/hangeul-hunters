-- Add AI Response Support
-- 1. Create conversation_topic table for storing topic and task information
CREATE TABLE conversation_topic (
    id BIGSERIAL PRIMARY KEY,
    conversation_type VARCHAR(50) NOT NULL,
    track VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    task_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

CREATE INDEX idx_conversation_topic_type_track ON conversation_topic(conversation_type, track);

-- 2. Create conversation_topic_task table for storing individual tasks
CREATE TABLE conversation_topic_task (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    level INT NOT NULL,
    name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

CREATE INDEX idx_topic_task_topic_id ON conversation_topic_task(topic_id);

-- 3. Add task tracking fields to conversation table
ALTER TABLE conversation ADD COLUMN task_current_level INT;
ALTER TABLE conversation ADD COLUMN task_all_completed BOOLEAN;

-- 4. Add AI response fields to message table
ALTER TABLE message ADD COLUMN reaction_emoji VARCHAR(50);
ALTER TABLE message ADD COLUMN reaction_description TEXT;
ALTER TABLE message ADD COLUMN reaction_reason TEXT;
ALTER TABLE message ADD COLUMN recommendation TEXT;

-- 5. Update message_feedback table - replace old fields with improvements JSON
ALTER TABLE message_feedback DROP COLUMN IF EXISTS explain;
ALTER TABLE message_feedback ADD COLUMN contents_feedback TEXT NOT NULL default '';
ALTER TABLE message_feedback ADD COLUMN nuance_feedback TEXT NOT NULL default '';

-- 6. Update conversation_feedback table - add JSONB improvements column
-- Drop old columns if they exist
ALTER TABLE conversation_feedback DROP COLUMN IF EXISTS improvement_examples;
ALTER TABLE conversation_feedback DROP COLUMN IF EXISTS improvement_points;

-- Add improvement_points as JSONB (JSON array of {point, tip} objects)
ALTER TABLE conversation_feedback ADD COLUMN improvement_points JSONB DEFAULT '[]'::jsonb;
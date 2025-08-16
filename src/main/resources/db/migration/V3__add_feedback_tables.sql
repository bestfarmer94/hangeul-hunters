-- Create message_feedback table
CREATE TABLE message_feedback (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    politeness_score INT NOT NULL,
    naturalness_score INT NOT NULL,
    pronunciation_score INT,
    appropriate_expression TEXT NOT NULL,
    explain TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

CREATE INDEX idx_message_feedback_message_id ON message_feedback(message_id);

-- Create conversation_feedback table
CREATE TABLE conversation_feedback (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    politeness_score INT NOT NULL,
    naturalness_score INT NOT NULL,
    pronunciation_score INT,
    summary TEXT NOT NULL,
    good_points TEXT NOT NULL,
    improvement_points TEXT NOT NULL,
    improvement_examples TEXT NOT NULL,
    overall_evaluation TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

CREATE INDEX idx_conversation_feedback_conversation_id ON conversation_feedback(conversation_id);
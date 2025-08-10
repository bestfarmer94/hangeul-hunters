-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    nickname VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    birth_date DATE NOT NULL,
    role VARCHAR(50) NOT NULL,
    provider VARCHAR(50),
    provider_id VARCHAR(255),
    korean_level VARCHAR(50),
    profile_image_url VARCHAR(1024),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);

-- Interest
CREATE TABLE interest (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    persona_id BIGINT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_interest_user_id ON interest(user_id);
CREATE INDEX idx_interest_persona_id ON interest(persona_id);

-- AI Persona
CREATE TABLE ai_persona (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    age INT NOT NULL,
    ai_role VARCHAR(255) NOT NULL,
    user_role VARCHAR(255),
    description TEXT,
    profile_image_url VARCHAR(1024),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_ai_persona_user_id ON ai_persona(user_id);

-- Conversation
CREATE TABLE conversation (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    persona_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    situation TEXT NOT NULL,
    chat_model_id VARCHAR(255),
    ended_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_conversation_user_id ON conversation(user_id);
CREATE INDEX idx_conversation_persona_id ON conversation(persona_id);

-- Message
CREATE TABLE message (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    translated_content TEXT,
    audio_url VARCHAR(1024),
    politeness_score INT,
    naturalness_score INT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_message_conversation_id ON message(conversation_id);
CREATE INDEX idx_message_created_at ON message(created_at);

-- Feedback
CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    target VARCHAR(32) NOT NULL,
    target_id BIGINT NOT NULL,
    honorific_score INT NOT NULL,
    naturalness_score INT NOT NULL,
    pronunciation_score INT,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_feedback_target ON feedback(target, target_id);
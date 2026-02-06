-- User Favorite Topic table
CREATE TABLE user_favorite_topic (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

CREATE INDEX idx_user_favorite_topic_user_id ON user_favorite_topic(user_id);

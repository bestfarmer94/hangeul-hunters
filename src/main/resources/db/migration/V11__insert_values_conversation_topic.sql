DROP TABLE conversation;

CREATE TABLE conversation (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    persona_id BIGINT NOT NULL,
    conversation_type VARCHAR(50) NOT NULL,
    conversation_topic VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    situation TEXT NOT NULL,
    chat_model_id VARCHAR(255),
    interview_company_name VARCHAR(255),
    interview_job_title VARCHAR(255),
    interview_job_posting TEXT,
    interview_style VARCHAR(50),
    task_current_level INT,
    task_current_name TEXT,
    task_all_completed BOOLEAN,
    ended_at TIMESTAMPTZ,
    last_activity_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

INSERT INTO conversation_topic (conversation_type, track, name, description, task_count, created_at) VALUES
('INTERVIEW', 'Career', 'Interview' , '면접', 3, NOW());

INSERT INTO conversation_topic_task (topic_id, level, name, created_at) VALUES
    (1, 1, '인사하기(임시)', NOW()),
    (1, 2, '질문답변하기(임시)', NOW()),
    (1, 3, '마지막인사하기(임시)', NOW());
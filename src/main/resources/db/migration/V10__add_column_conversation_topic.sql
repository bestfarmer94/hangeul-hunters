ALTER TABLE conversation_topic ADD COLUMN created_by BIGINT;
ALTER TABLE conversation_topic ADD COLUMN updated_by BIGINT;
ALTER TABLE conversation_topic ADD COLUMN deleted_by BIGINT;
ALTER TABLE conversation_topic_task ADD COLUMN created_by BIGINT;
ALTER TABLE conversation_topic_task ADD COLUMN updated_by BIGINT;
ALTER TABLE conversation_topic_task ADD COLUMN deleted_by BIGINT;
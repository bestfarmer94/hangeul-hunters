ALTER TABLE conversation DROP COLUMN task_current_level;
ALTER TABLE conversation DROP COLUMN task_all_completed;

ALTER TABLE conversation ADD COLUMN task_current_level INT;
ALTER TABLE conversation ADD COLUMN task_current_name TEXT;
ALTER TABLE conversation ADD COLUMN task_all_completed BOOLEAN;
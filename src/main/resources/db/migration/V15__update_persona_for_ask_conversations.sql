-- Make age column nullable in ai_persona table
ALTER TABLE ai_persona ALTER COLUMN age DROP NOT NULL;

-- Insert persona with ID 0 for ASK type conversations
INSERT INTO ai_persona (id, user_id, name, gender, age, ai_role, user_role, description, voice, created_at)
VALUES (0, NULL, 'System', 'NONE', NULL, 'assistant', NULL, 'System assistant', 'ko-KR-Wavenet-D', NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence to avoid conflicts (ensure next auto-generated ID starts from 1 or higher)
SELECT setval('ai_persona_id_seq', GREATEST(1, (SELECT MAX(id) FROM ai_persona WHERE id > 0)), true);

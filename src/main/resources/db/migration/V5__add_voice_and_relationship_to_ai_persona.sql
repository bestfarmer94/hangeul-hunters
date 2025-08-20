-- Add voice column to ai_persona table
ALTER TABLE ai_persona ADD COLUMN voice VARCHAR(255) NULL;

-- Initialize voice for existing records
UPDATE ai_persona SET voice = 'ko-KR-Wavenet-C' WHERE ai_role = 'boss' and gender = 'MALE';
UPDATE ai_persona SET voice = 'ko-KR-Wavenet-B' WHERE ai_role = 'boss' and gender = 'FEMALE';
UPDATE ai_persona SET voice = 'ko-KR-Wavenet-C' WHERE ai_role = 'girlfriend''s parents' and gender = 'MALE';
UPDATE ai_persona SET voice = 'ko-KR-Wavenet-B' WHERE ai_role = 'girlfriend''s parents' and gender = 'FEMALE';
UPDATE ai_persona SET voice = 'ko-KR-Wavenet-D' WHERE ai_role = 'cafe clerk' and gender = 'MALE';
UPDATE ai_persona SET voice = 'ko-KR-Chirp3-HD-Leda' WHERE ai_role = 'cafe clerk' and gender = 'FEMALE';

-- Make voice column NOT NULL after initializing data
ALTER TABLE ai_persona ALTER COLUMN voice SET NOT NULL;

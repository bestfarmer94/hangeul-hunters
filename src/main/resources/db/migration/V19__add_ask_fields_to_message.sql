-- Add approach_tip and cultural_insight columns to message table for Ask conversations
ALTER TABLE message ADD COLUMN ask_approach_tip TEXT;
ALTER TABLE message ADD COLUMN ask_cultural_insight TEXT;

-- Add can_get_report column to conversation table
ALTER TABLE conversation
ADD COLUMN can_get_report BOOLEAN DEFAULT FALSE;

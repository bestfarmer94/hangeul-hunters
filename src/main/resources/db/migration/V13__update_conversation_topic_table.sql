-- Add ASK-specific fields to conversation table
ALTER TABLE conversation_topic ADD COLUMN image_url VARCHAR(1024);
ALTER TABLE conversation_topic ADD COLUMN display_order INT NOT NULL DEFAULT 0;

CREATE INDEX idx_conversation_topic_track_order ON conversation_topic(track, display_order);

-- Insert sample topics for RolePlaying
INSERT INTO conversation_topic (id, conversation_type, name, track, description, image_url, display_order, created_at) VALUES
(1, 'ROLE_PLAYING', 'Job Interview', 'Career', 'Practice real interview moments—clearly, calmly, and with confidence.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 0, NOW()),
(2, 'ROLE_PLAYING', 'Asking for Help', 'Career', 'Practice asking for help clearly and politely at work.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 1, NOW()),
(3, 'ROLE_PLAYING', 'Morning Greeting', 'Career', 'Start the workday with a natural greeting.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 2, NOW()),
(4, 'ROLE_PLAYING', 'Small Talk at Work', 'Career', 'Make light conversation during a short work break.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 3, NOW()),
(5, 'ROLE_PLAYING', 'Setting Work Boundaries', 'Career', 'Say no or set limits without sounding rude.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 4, NOW()),
(6, 'ROLE_PLAYING', 'Explaining Your Choice', 'Family', 'Explain your decisions calmly and clearly.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 0, NOW()),
(7, 'ROLE_PLAYING', 'Sensitive Topics, Softly', 'Family', 'Bring up sensitive topics without hurting feelings.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 1, NOW()),
(8, 'ROLE_PLAYING', 'Morning at Home', 'Family', 'Start the day with a warm family greeting.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 2, NOW()),
(9, 'ROLE_PLAYING', 'Gratitude, Grown-Up Style', 'Family', 'Express thanks in a polite, adult way.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 3, NOW()),
(10, 'ROLE_PLAYING', 'Family Mealtime', 'Family', 'Make casual conversation during family meals.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 4, NOW()),
(11, 'ROLE_PLAYING', 'Midnight Thanks', 'Belonging', 'Thank someone warmly after late-night help.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 0, NOW()),
(12, 'ROLE_PLAYING', 'Kind but Pressured', 'Belonging', 'Say no kindly when you feel pressured.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 1, NOW()),
(13, 'ROLE_PLAYING', 'Polite to Playful', 'Belonging', 'Move from polite talk to light jokes.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 2, NOW()),
(14, 'ROLE_PLAYING', 'First Drink Vibes', 'Belonging', 'Find the right tone at a first group drink.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 3, NOW()),
(15, 'ROLE_PLAYING', 'Making Peace Gently', 'Belonging', 'Talk about hurt feelings without drama.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 4, NOW()),
(16, 'ROLE_PLAYING', 'Talking About Your Bias', 'K-POP', 'Talk about your favorite idol naturally.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 0, NOW()),
(17, 'ROLE_PLAYING', 'Playlist Share', 'K-POP', 'Practice asking for help clearly and politely at work.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 1, NOW()),
(18, 'ROLE_PLAYING', 'Filming a Challenge Reel', 'K-POP', 'Talk about filming a dance or challenge reel together.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 2, NOW()),
(19, 'ROLE_PLAYING', 'Light Fan Slang', 'K-POP', 'Use fan slang lightly and naturally.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 3, NOW()),
(20, 'ROLE_PLAYING', 'That Hurt, Softly', 'K-POP', 'Share concert memories in a fun, natural way.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 4, NOW());

-- Reset sequence to avoid conflicts (ensure next auto-generated ID starts from 1 or higher)
SELECT setval('conversation_topic_id_seq', GREATEST(1, (SELECT MAX(id) FROM conversation_topic WHERE id > 0)), true);
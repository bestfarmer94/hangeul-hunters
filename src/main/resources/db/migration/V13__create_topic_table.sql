-- Topic table for RolePlaying conversation selection
CREATE TABLE topic (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(1024),
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

CREATE INDEX idx_topic_category_order ON topic(category, display_order);

-- Insert sample topics for RolePlaying
INSERT INTO topic (name, category, description, image_url, display_order, created_at, created_by) VALUES
('Asking for Help', 'Career', 'Practice asking for help clearly and politely at work.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 0, NOW(), 0),
('Morning Greeting', 'Career', 'Start the workday with a natural greeting.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 1, NOW(), 0),
('Small Talk at Work', 'Career', 'Make light conversation during a short work break.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 2, NOW(), 0),
('Setting Work Boundaries', 'Career', 'Say no or set limits without sounding rude.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 3, NOW(), 0),
('Explaining Your Choice', 'Family', 'Explain your decisions calmly and clearly.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 0, NOW(), 0),
('Sensitive Topics, Softly', 'Family', 'Bring up sensitive topics without hurting feelings.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 1, NOW(), 0),
('Morning at Home', 'Family', 'Start the day with a warm family greeting.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 2, NOW(), 0),
('Gratitude, Grown-Up Style', 'Family', 'Express thanks in a polite, adult way.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 3, NOW(), 0),
('Family Mealtime', 'Family', 'Make casual conversation during family meals.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 4, NOW(), 0),
('Midnight Thanks', 'Belonging', 'Thank someone warmly after late-night help.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 0, NOW(), 0),
('Kind but Pressured', 'Belonging', 'Say no kindly when you feel pressured.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 1, NOW(), 0),
('Polite to Playful', 'Belonging', 'Move from polite talk to light jokes.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 2, NOW(), 0),
('First Drink Vibes', 'Belonging', 'Find the right tone at a first group drink.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 3, NOW(), 0),
('Making Peace Gently', 'Belonging', 'Talk about hurt feelings without drama.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 4, NOW(), 0),
('Talking About Your Bias', 'K-POP', 'Talk about your favorite idol naturally.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 0, NOW(), 0),
('Playlist Share', 'K-POP', 'Practice asking for help clearly and politely at work.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 1, NOW(), 0),
('Filming a Challenge Reel', 'K-POP', 'Talk about filming a dance or challenge reel together.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 2, NOW(), 0),
('Light Fan Slang', 'K-POP', 'Use fan slang lightly and naturally.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 3, NOW(), 0),
('That Hurt, Softly', 'K-POP', 'Share concert memories in a fun, natural way.', 'https://cdn.imweb.me/upload/S20210927e14f9f0cf71bc/388bdeca26d50.png', 4, NOW(), 0);

-- Simple test INSERT
INSERT INTO likes (target_type, target_id, user_id, created_at) VALUES
('POST', 1, 2, NOW() - INTERVAL '1 day');

-- Mock Likes, Follows, and Bookmarks

-- Likes on Posts
INSERT INTO likes (user_id, target_type, target_id, created_at) VALUES
-- Post 1 likes
(3, 'POST', 1, NOW() - INTERVAL '3 days'), (5, 'POST', 1, NOW() - INTERVAL '3 days'),
(7, 'POST', 1, NOW() - INTERVAL '3 days'), (8, 'POST', 1, NOW() - INTERVAL '2 days'),
-- Post 2 likes
(1, 'POST', 2, NOW() - INTERVAL '5 days'), (4, 'POST', 2, NOW() - INTERVAL '5 days'),
(6, 'POST', 2, NOW() - INTERVAL '4 days'), (8, 'POST', 2, NOW() - INTERVAL '4 days'),
-- Post 7 likes
(2, 'POST', 7, NOW() - INTERVAL '8 days'), (3, 'POST', 7, NOW() - INTERVAL '7 days'),
(5, 'POST', 7, NOW() - INTERVAL '7 days'), (6, 'POST', 7, NOW() - INTERVAL '6 days'),
-- Post 9 likes
(1, 'POST', 9, NOW() - INTERVAL '9 days'), (4, 'POST', 9, NOW() - INTERVAL '8 days'),
(5, 'POST', 9, NOW() - INTERVAL '8 days'), (7, 'POST', 9, NOW() - INTERVAL '7 days'),
-- Post 13 likes
(1, 'POST', 13, NOW() - INTERVAL '11 days'), (3, 'POST', 13, NOW() - INTERVAL '10 days'),
(5, 'POST', 13, NOW() - INTERVAL '10 days'), (7, 'POST', 13, NOW() - INTERVAL '9 days'),
-- Post 16 likes
(1, 'POST', 16, NOW() - INTERVAL '2 days'), (2, 'POST', 16, NOW() - INTERVAL '2 days'),
(4, 'POST', 16, NOW() - INTERVAL '1 day'), (5, 'POST', 16, NOW() - INTERVAL '1 day'),
-- Post 17 likes
(1, 'POST', 17, NOW() - INTERVAL '14 days'), (3, 'POST', 17, NOW() - INTERVAL '13 days'),
(5, 'POST', 17, NOW() - INTERVAL '12 days'), (6, 'POST', 17, NOW() - INTERVAL '11 days');

-- Likes on Comments
INSERT INTO likes (user_id, target_type, target_id, created_at) VALUES
(2, 'COMMENT', 1, NOW() - INTERVAL '3 days'), (4, 'COMMENT', 1, NOW() - INTERVAL '3 days'),
(3, 'COMMENT', 4, NOW() - INTERVAL '5 days'), (5, 'COMMENT', 4, NOW() - INTERVAL '5 days'),
(1, 'COMMENT', 6, NOW() - INTERVAL '5 days'), (4, 'COMMENT', 6, NOW() - INTERVAL '5 days'),
(2, 'COMMENT', 8, NOW() - INTERVAL '8 days'), (3, 'COMMENT', 8, NOW() - INTERVAL '8 days'),
(1, 'COMMENT', 11, NOW() - INTERVAL '4 days'), (6, 'COMMENT', 11, NOW() - INTERVAL '4 days'),
(3, 'COMMENT', 15, NOW() - INTERVAL '9 days'), (7, 'COMMENT', 15, NOW() - INTERVAL '9 days'),
(8, 'COMMENT', 24, NOW() - INTERVAL '11 days'), (9, 'COMMENT', 24, NOW() - INTERVAL '10 days'),
(1, 'COMMENT', 28, NOW() - INTERVAL '6 days'), (4, 'COMMENT', 28, NOW() - INTERVAL '6 days'),
(2, 'COMMENT', 35, NOW() - INTERVAL '14 days'), (6, 'COMMENT', 35, NOW() - INTERVAL '13 days'),
(3, 'COMMENT', 39, NOW() - INTERVAL '1 day'), (5, 'COMMENT', 39, NOW() - INTERVAL '1 day'),
(1, 'COMMENT', 42, NOW() - INTERVAL '8 days'), (7, 'COMMENT', 42, NOW() - INTERVAL '8 days'),
(4, 'COMMENT', 45, NOW() - INTERVAL '3 days'), (6, 'COMMENT', 45, NOW() - INTERVAL '3 days');

-- Follows (mutual follows between active users)
INSERT INTO follow (follower_id, following_id, created_at) VALUES
-- 김개발 follows
(1, 2, NOW() - INTERVAL '5 months'), (1, 3, NOW() - INTERVAL '4 months'),
(1, 5, NOW() - INTERVAL '3 months'), (1, 7, NOW() - INTERVAL '2 months'),
-- 이스프링 follows
(2, 1, NOW() - INTERVAL '5 months'), (2, 3, NOW() - INTERVAL '4 months'),
(2, 4, NOW() - INTERVAL '3 months'), (2, 8, NOW() - INTERVAL '2 months'),
-- 박자바 follows
(3, 1, NOW() - INTERVAL '4 months'), (3, 2, NOW() - INTERVAL '4 months'),
(3, 5, NOW() - INTERVAL '3 months'), (3, 7, NOW() - INTERVAL '2 months'),
-- 최리액트 follows
(4, 2, NOW() - INTERVAL '3 months'), (4, 8, NOW() - INTERVAL '2 months'),
(4, 1, NOW() - INTERVAL '2 months'), (4, 6, NOW() - INTERVAL '1 month'),
-- DB정 follows
(5, 1, NOW() - INTERVAL '3 months'), (5, 3, NOW() - INTERVAL '3 months'),
(5, 7, NOW() - INTERVAL '2 months'), (5, 9, NOW() - INTERVAL '1 month'),
-- 강데브옵스 follows
(6, 7, NOW() - INTERVAL '2 months'), (6, 9, NOW() - INTERVAL '1 month'),
(6, 1, NOW() - INTERVAL '2 months'), (6, 5, NOW() - INTERVAL '1 month'),
-- 오백엔드 follows
(7, 1, NOW() - INTERVAL '5 months'), (7, 3, NOW() - INTERVAL '4 months'),
(7, 5, NOW() - INTERVAL '3 months'), (7, 6, NOW() - INTERVAL '2 months'),
-- 윤풀스택 follows
(8, 2, NOW() - INTERVAL '3 months'), (8, 4, NOW() - INTERVAL '2 months'),
(8, 1, NOW() - INTERVAL '2 months'), (8, 9, NOW() - INTERVAL '1 month'),
-- 한클라우드 follows
(9, 6, NOW() - INTERVAL '1 month'), (9, 7, NOW() - INTERVAL '1 month'),
(9, 1, NOW() - INTERVAL '1 month'), (9, 5, NOW() - INTERVAL '1 month');

-- Bookmarks (users bookmark interesting posts)
INSERT INTO bookmark (user_id, board_id, created_at) VALUES
-- 김개발 bookmarks
(1, 2, NOW() - INTERVAL '5 days'), (1, 7, NOW() - INTERVAL '8 days'),
(1, 9, NOW() - INTERVAL '9 days'), (1, 13, NOW() - INTERVAL '11 days'),
-- 이스프링 bookmarks
(2, 1, NOW() - INTERVAL '3 days'), (2, 4, NOW() - INTERVAL '10 days'),
(2, 12, NOW() - INTERVAL '5 days'), (2, 16, NOW() - INTERVAL '2 days'),
-- 박자바 bookmarks
(3, 2, NOW() - INTERVAL '5 days'), (3, 8, NOW() - INTERVAL '4 days'),
(3, 10, NOW() - INTERVAL '12 days'), (3, 17, NOW() - INTERVAL '14 days'),
-- 최리액트 bookmarks
(4, 5, NOW() - INTERVAL '2 days'), (4, 6, NOW() - INTERVAL '6 days'),
(4, 7, NOW() - INTERVAL '8 days'), (4, 16, NOW() - INTERVAL '2 days'),
-- DB정 bookmarks
(5, 8, NOW() - INTERVAL '4 days'), (5, 9, NOW() - INTERVAL '9 days'),
(5, 10, NOW() - INTERVAL '12 days'), (5, 2, NOW() - INTERVAL '5 days'),
-- 강데브옵스 bookmarks
(6, 11, NOW() - INTERVAL '1 day'), (6, 12, NOW() - INTERVAL '5 days'),
(6, 13, NOW() - INTERVAL '11 days'), (6, 17, NOW() - INTERVAL '14 days'),
-- 오백엔드 bookmarks
(7, 1, NOW() - INTERVAL '3 days'), (7, 2, NOW() - INTERVAL '5 days'),
(7, 4, NOW() - INTERVAL '10 days'), (7, 15, NOW() - INTERVAL '13 days'),
-- 윤풀스택 bookmarks
(8, 5, NOW() - INTERVAL '2 days'), (8, 7, NOW() - INTERVAL '8 days'),
(8, 16, NOW() - INTERVAL '2 days'), (8, 6, NOW() - INTERVAL '6 days'),
-- 한클라우드 bookmarks
(9, 11, NOW() - INTERVAL '1 day'), (9, 12, NOW() - INTERVAL '5 days'),
(9, 13, NOW() - INTERVAL '11 days'), (9, 17, NOW() - INTERVAL '14 days');

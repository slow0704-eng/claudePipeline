-- 게시글 좋아요 생성 (board_like)
-- 인기 게시글에 다양한 사용자들이 좋아요

-- Board ID 1 (Spring Boot 3.0 새로운 기능 정리) - 23 likes
INSERT INTO likes (target_type, target_id, user_id, created_at) VALUES
('POST', 1, 2, NOW() - INTERVAL '29 days'),
('POST', 1, 3, NOW() - INTERVAL '28 days'),
('POST', 1, 4, NOW() - INTERVAL '27 days'),
('POST', 1, 5, NOW() - INTERVAL '26 days'),
('POST', 1, 6, NOW() - INTERVAL '25 days'),
('POST', 1, 7, NOW() - INTERVAL '24 days'),
('POST', 1, 8, NOW() - INTERVAL '23 days'),
('POST', 1, 9, NOW() - INTERVAL '22 days'),
('POST', 1, 10, NOW() - INTERVAL '21 days'),
('POST', 1, 11, NOW() - INTERVAL '20 days'),
('POST', 1, 12, NOW() - INTERVAL '19 days'),
('POST', 1, 13, NOW() - INTERVAL '18 days'),
('POST', 1, 14, NOW() - INTERVAL '17 days'),
('POST', 1, 15, NOW() - INTERVAL '16 days'),
('POST', 1, 16, NOW() - INTERVAL '15 days'),
('POST', 1, 17, NOW() - INTERVAL '14 days'),
('POST', 1, 18, NOW() - INTERVAL '13 days'),
('POST', 1, 19, NOW() - INTERVAL '12 days'),
('POST', 1, 20, NOW() - INTERVAL '11 days'),
('POST', 1, 21, NOW() - INTERVAL '10 days'),
('POST', 1, 22, NOW() - INTERVAL '9 days'),
('POST', 1, 23, NOW() - INTERVAL '8 days'),
('POST', 1, 24, NOW() - INTERVAL '7 days');

-- Board ID 2 (JPA N+1 문제 해결 방법) - 34 likes
INSERT INTO likes (target_type, target_id, user_id, created_at) VALUES
('POST', 2, 2, NOW() - INTERVAL '27 days'),
('POST', 2, 3, NOW() - INTERVAL '26 days'),
('POST', 2, 4, NOW() - INTERVAL '25 days'),
('POST', 2, 5, NOW() - INTERVAL '24 days'),
('POST', 2, 6, NOW() - INTERVAL '23 days'),
('POST', 2, 7, NOW() - INTERVAL '22 days'),
('POST', 2, 8, NOW() - INTERVAL '21 days'),
('POST', 2, 9, NOW() - INTERVAL '20 days'),
('POST', 2, 10, NOW() - INTERVAL '19 days'),
('POST', 2, 11, NOW() - INTERVAL '18 days'),
('POST', 2, 12, NOW() - INTERVAL '17 days'),
('POST', 2, 13, NOW() - INTERVAL '16 days'),
('POST', 2, 14, NOW() - INTERVAL '15 days'),
('POST', 2, 15, NOW() - INTERVAL '14 days'),
('POST', 2, 16, NOW() - INTERVAL '13 days'),
('POST', 2, 17, NOW() - INTERVAL '12 days'),
('POST', 2, 18, NOW() - INTERVAL '11 days'),
('POST', 2, 19, NOW() - INTERVAL '10 days'),
('POST', 2, 20, NOW() - INTERVAL '9 days'),
('POST', 2, 21, NOW() - INTERVAL '8 days'),
('POST', 2, 22, NOW() - INTERVAL '7 days'),
('POST', 2, 23, NOW() - INTERVAL '6 days'),
('POST', 2, 24, NOW() - INTERVAL '5 days'),
('POST', 2, 25, NOW() - INTERVAL '4 days'),
('POST', 2, 26, NOW() - INTERVAL '3 days'),
('POST', 2, 27, NOW() - INTERVAL '2 days'),
('POST', 2, 28, NOW() - INTERVAL '1 day'),
('POST', 2, 29, NOW() - INTERVAL '20 hours'),
('POST', 2, 30, NOW() - INTERVAL '15 hours'),
('POST', 2, 31, NOW() - INTERVAL '10 hours'),
('POST', 2, 32, NOW() - INTERVAL '8 hours'),
('POST', 2, 33, NOW() - INTERVAL '6 hours'),
('POST', 2, 34, NOW() - INTERVAL '4 hours'),
('POST', 2, 35, NOW() - INTERVAL '2 hours');

-- Board ID 3~10 - Various likes (15-45 likes each)
INSERT INTO likes (target_type, target_id, user_id, created_at) VALUES
-- Board 3 (41 likes)
('POST', 3, 2, NOW() - INTERVAL '25 days'), ('POST', 3, 3, NOW() - INTERVAL '24 days'), ('POST', 3, 4, NOW() - INTERVAL '23 days'),
('POST', 3, 5, NOW() - INTERVAL '22 days'), ('POST', 3, 6, NOW() - INTERVAL '21 days'), ('POST', 3, 7, NOW() - INTERVAL '20 days'),
('POST', 3, 8, NOW() - INTERVAL '19 days'), ('POST', 3, 9, NOW() - INTERVAL '18 days'), ('POST', 3, 10, NOW() - INTERVAL '17 days'),
('POST', 3, 11, NOW() - INTERVAL '16 days'), ('POST', 3, 12, NOW() - INTERVAL '15 days'), ('POST', 3, 13, NOW() - INTERVAL '14 days'),
('POST', 3, 14, NOW() - INTERVAL '13 days'), ('POST', 3, 15, NOW() - INTERVAL '12 days'), ('POST', 3, 16, NOW() - INTERVAL '11 days'),
('POST', 3, 17, NOW() - INTERVAL '10 days'), ('POST', 3, 18, NOW() - INTERVAL '9 days'), ('POST', 3, 19, NOW() - INTERVAL '8 days'),
('POST', 3, 20, NOW() - INTERVAL '7 days'), ('POST', 3, 21, NOW() - INTERVAL '6 days'), ('POST', 3, 22, NOW() - INTERVAL '5 days'),
('POST', 3, 23, NOW() - INTERVAL '4 days'), ('POST', 3, 24, NOW() - INTERVAL '3 days'), ('POST', 3, 25, NOW() - INTERVAL '2 days'),
('POST', 3, 26, NOW() - INTERVAL '1 day'), ('POST', 3, 27, NOW() - INTERVAL '20 hours'), ('POST', 3, 28, NOW() - INTERVAL '18 hours'),
('POST', 3, 29, NOW() - INTERVAL '16 hours'), ('POST', 3, 30, NOW() - INTERVAL '14 hours'), ('POST', 3, 31, NOW() - INTERVAL '12 hours'),
('POST', 3, 32, NOW() - INTERVAL '10 hours'), ('POST', 3, 33, NOW() - INTERVAL '8 hours'), ('POST', 3, 34, NOW() - INTERVAL '6 hours'),
('POST', 3, 35, NOW() - INTERVAL '4 hours'), ('POST', 3, 36, NOW() - INTERVAL '2 hours'), ('POST', 3, 37, NOW() - INTERVAL '1 hour'),
('POST', 3, 38, NOW() - INTERVAL '30 minutes'), ('POST', 3, 39, NOW() - INTERVAL '15 minutes'), ('POST', 3, 40, NOW() - INTERVAL '10 minutes'),
('POST', 3, 41, NOW() - INTERVAL '5 minutes'), ('POST', 3, 42, NOW() - INTERVAL '1 minute'),

-- Board 4 (28 likes)
('POST', 4, 3, NOW() - INTERVAL '23 days'), ('POST', 4, 4, NOW() - INTERVAL '22 days'), ('POST', 4, 5, NOW() - INTERVAL '21 days'),
('POST', 4, 6, NOW() - INTERVAL '20 days'), ('POST', 4, 7, NOW() - INTERVAL '19 days'), ('POST', 4, 8, NOW() - INTERVAL '18 days'),
('POST', 4, 9, NOW() - INTERVAL '17 days'), ('POST', 4, 10, NOW() - INTERVAL '16 days'), ('POST', 4, 11, NOW() - INTERVAL '15 days'),
('POST', 4, 12, NOW() - INTERVAL '14 days'), ('POST', 4, 13, NOW() - INTERVAL '13 days'), ('POST', 4, 14, NOW() - INTERVAL '12 days'),
('POST', 4, 15, NOW() - INTERVAL '11 days'), ('POST', 4, 16, NOW() - INTERVAL '10 days'), ('POST', 4, 17, NOW() - INTERVAL '9 days'),
('POST', 4, 18, NOW() - INTERVAL '8 days'), ('POST', 4, 19, NOW() - INTERVAL '7 days'), ('POST', 4, 20, NOW() - INTERVAL '6 days'),
('POST', 4, 21, NOW() - INTERVAL '5 days'), ('POST', 4, 22, NOW() - INTERVAL '4 days'), ('POST', 4, 23, NOW() - INTERVAL '3 days'),
('POST', 4, 24, NOW() - INTERVAL '2 days'), ('POST', 4, 25, NOW() - INTERVAL '1 day'), ('POST', 4, 26, NOW() - INTERVAL '18 hours'),
('POST', 4, 27, NOW() - INTERVAL '12 hours'), ('POST', 4, 28, NOW() - INTERVAL '8 hours'), ('POST', 4, 29, NOW() - INTERVAL '4 hours'),
('POST', 4, 30, NOW() - INTERVAL '2 hours'),

-- Board 5 (37 likes)
('POST', 5, 2, NOW() - INTERVAL '21 days'), ('POST', 5, 3, NOW() - INTERVAL '20 days'), ('POST', 5, 4, NOW() - INTERVAL '19 days'),
('POST', 5, 5, NOW() - INTERVAL '18 days'), ('POST', 5, 6, NOW() - INTERVAL '17 days'), ('POST', 5, 7, NOW() - INTERVAL '16 days'),
('POST', 5, 8, NOW() - INTERVAL '15 days'), ('POST', 5, 9, NOW() - INTERVAL '14 days'), ('POST', 5, 10, NOW() - INTERVAL '13 days'),
('POST', 5, 11, NOW() - INTERVAL '12 days'), ('POST', 5, 12, NOW() - INTERVAL '11 days'), ('POST', 5, 13, NOW() - INTERVAL '10 days'),
('POST', 5, 14, NOW() - INTERVAL '9 days'), ('POST', 5, 15, NOW() - INTERVAL '8 days'), ('POST', 5, 16, NOW() - INTERVAL '7 days'),
('POST', 5, 17, NOW() - INTERVAL '6 days'), ('POST', 5, 18, NOW() - INTERVAL '5 days'), ('POST', 5, 19, NOW() - INTERVAL '4 days'),
('POST', 5, 20, NOW() - INTERVAL '3 days'), ('POST', 5, 21, NOW() - INTERVAL '2 days'), ('POST', 5, 22, NOW() - INTERVAL '1 day'),
('POST', 5, 23, NOW() - INTERVAL '20 hours'), ('POST', 5, 24, NOW() - INTERVAL '18 hours'), ('POST', 5, 25, NOW() - INTERVAL '16 hours'),
('POST', 5, 26, NOW() - INTERVAL '14 hours'), ('POST', 5, 27, NOW() - INTERVAL '12 hours'), ('POST', 5, 28, NOW() - INTERVAL '10 hours'),
('POST', 5, 29, NOW() - INTERVAL '8 hours'), ('POST', 5, 30, NOW() - INTERVAL '6 hours'), ('POST', 5, 31, NOW() - INTERVAL '4 hours'),
('POST', 5, 32, NOW() - INTERVAL '3 hours'), ('POST', 5, 33, NOW() - INTERVAL '2 hours'), ('POST', 5, 34, NOW() - INTERVAL '1 hour'),
('POST', 5, 35, NOW() - INTERVAL '45 minutes'), ('POST', 5, 36, NOW() - INTERVAL '30 minutes'), ('POST', 5, 37, NOW() - INTERVAL '15 minutes'),
('POST', 5, 38, NOW() - INTERVAL '5 minutes'),

-- Board 6-20 - Medium popularity posts (10-30 likes each)
('POST', 6, 2, NOW() - INTERVAL '19 days'), ('POST', 6, 3, NOW() - INTERVAL '18 days'), ('POST', 6, 4, NOW() - INTERVAL '17 days'),
('POST', 6, 5, NOW() - INTERVAL '16 days'), ('POST', 6, 6, NOW() - INTERVAL '15 days'), ('POST', 6, 7, NOW() - INTERVAL '14 days'),
('POST', 6, 8, NOW() - INTERVAL '13 days'), ('POST', 6, 9, NOW() - INTERVAL '12 days'), ('POST', 6, 10, NOW() - INTERVAL '11 days'),
('POST', 6, 11, NOW() - INTERVAL '10 days'), ('POST', 6, 12, NOW() - INTERVAL '9 days'), ('POST', 6, 13, NOW() - INTERVAL '8 days'),
('POST', 6, 14, NOW() - INTERVAL '7 days'), ('POST', 6, 15, NOW() - INTERVAL '6 days'), ('POST', 6, 16, NOW() - INTERVAL '5 days'),

('POST', 7, 3, NOW() - INTERVAL '17 days'), ('POST', 7, 4, NOW() - INTERVAL '16 days'), ('POST', 7, 5, NOW() - INTERVAL '15 days'),
('POST', 7, 6, NOW() - INTERVAL '14 days'), ('POST', 7, 7, NOW() - INTERVAL '13 days'), ('POST', 7, 8, NOW() - INTERVAL '12 days'),
('POST', 7, 9, NOW() - INTERVAL '11 days'), ('POST', 7, 10, NOW() - INTERVAL '10 days'), ('POST', 7, 11, NOW() - INTERVAL '9 days'),
('POST', 7, 12, NOW() - INTERVAL '8 days'), ('POST', 7, 13, NOW() - INTERVAL '7 days'), ('POST', 7, 14, NOW() - INTERVAL '6 days'),
('POST', 7, 15, NOW() - INTERVAL '5 days'), ('POST', 7, 16, NOW() - INTERVAL '4 days'), ('POST', 7, 17, NOW() - INTERVAL '3 days'),
('POST', 7, 18, NOW() - INTERVAL '2 days'), ('POST', 7, 19, NOW() - INTERVAL '1 day'), ('POST', 7, 20, NOW() - INTERVAL '12 hours'),

('POST', 8, 2, NOW() - INTERVAL '15 days'), ('POST', 8, 3, NOW() - INTERVAL '14 days'), ('POST', 8, 4, NOW() - INTERVAL '13 days'),
('POST', 8, 5, NOW() - INTERVAL '12 days'), ('POST', 8, 6, NOW() - INTERVAL '11 days'), ('POST', 8, 7, NOW() - INTERVAL '10 days'),
('POST', 8, 8, NOW() - INTERVAL '9 days'), ('POST', 8, 9, NOW() - INTERVAL '8 days'), ('POST', 8, 10, NOW() - INTERVAL '7 days'),
('POST', 8, 11, NOW() - INTERVAL '6 days'), ('POST', 8, 12, NOW() - INTERVAL '5 days'), ('POST', 8, 13, NOW() - INTERVAL '4 days'),
('POST', 8, 14, NOW() - INTERVAL '3 days'), ('POST', 8, 15, NOW() - INTERVAL '2 days'), ('POST', 8, 16, NOW() - INTERVAL '1 day'),
('POST', 8, 17, NOW() - INTERVAL '12 hours'), ('POST', 8, 18, NOW() - INTERVAL '6 hours'), ('POST', 8, 19, NOW() - INTERVAL '3 hours'),

('POST', 9, 3, NOW() - INTERVAL '13 days'), ('POST', 9, 4, NOW() - INTERVAL '12 days'), ('POST', 9, 5, NOW() - INTERVAL '11 days'),
('POST', 9, 6, NOW() - INTERVAL '10 days'), ('POST', 9, 7, NOW() - INTERVAL '9 days'), ('POST', 9, 8, NOW() - INTERVAL '8 days'),
('POST', 9, 9, NOW() - INTERVAL '7 days'), ('POST', 9, 10, NOW() - INTERVAL '6 days'), ('POST', 9, 11, NOW() - INTERVAL '5 days'),
('POST', 9, 12, NOW() - INTERVAL '4 days'), ('POST', 9, 13, NOW() - INTERVAL '3 days'), ('POST', 9, 14, NOW() - INTERVAL '2 days'),
('POST', 9, 15, NOW() - INTERVAL '1 day'), ('POST', 9, 16, NOW() - INTERVAL '12 hours'), ('POST', 9, 17, NOW() - INTERVAL '6 hours'),

('POST', 10, 2, NOW() - INTERVAL '11 days'), ('POST', 10, 3, NOW() - INTERVAL '10 days'), ('POST', 10, 4, NOW() - INTERVAL '9 days'),
('POST', 10, 5, NOW() - INTERVAL '8 days'), ('POST', 10, 6, NOW() - INTERVAL '7 days'), ('POST', 10, 7, NOW() - INTERVAL '6 days'),
('POST', 10, 8, NOW() - INTERVAL '5 days'), ('POST', 10, 9, NOW() - INTERVAL '4 days'), ('POST', 10, 10, NOW() - INTERVAL '3 days'),
('POST', 10, 11, NOW() - INTERVAL '2 days'), ('POST', 10, 12, NOW() - INTERVAL '1 day'), ('POST', 10, 13, NOW() - INTERVAL '12 hours'),

-- 나머지 게시글들에도 소수의 좋아요 추가
('POST', 11, 2, NOW() - INTERVAL '9 days'), ('POST', 11, 3, NOW() - INTERVAL '8 days'), ('POST', 11, 4, NOW() - INTERVAL '7 days'),
('POST', 11, 5, NOW() - INTERVAL '6 days'), ('POST', 11, 6, NOW() - INTERVAL '5 days'), ('POST', 11, 7, NOW() - INTERVAL '4 days'),
('POST', 11, 8, NOW() - INTERVAL '3 days'), ('POST', 11, 9, NOW() - INTERVAL '2 days'),

('POST', 12, 3, NOW() - INTERVAL '7 days'), ('POST', 12, 4, NOW() - INTERVAL '6 days'), ('POST', 12, 5, NOW() - INTERVAL '5 days'),
('POST', 12, 6, NOW() - INTERVAL '4 days'), ('POST', 12, 7, NOW() - INTERVAL '3 days'), ('POST', 12, 8, NOW() - INTERVAL '2 days'),
('POST', 12, 9, NOW() - INTERVAL '1 day'),

('POST', 13, 2, NOW() - INTERVAL '5 days'), ('POST', 13, 3, NOW() - INTERVAL '4 days'), ('POST', 13, 4, NOW() - INTERVAL '3 days'),
('POST', 13, 5, NOW() - INTERVAL '2 days'), ('POST', 13, 6, NOW() - INTERVAL '1 day'),

('POST', 14, 3, NOW() - INTERVAL '4 days'), ('POST', 14, 4, NOW() - INTERVAL '3 days'), ('POST', 14, 5, NOW() - INTERVAL '2 days'),
('POST', 14, 6, NOW() - INTERVAL '1 day'),

('POST', 15, 2, NOW() - INTERVAL '3 days'), ('POST', 15, 3, NOW() - INTERVAL '2 days'), ('POST', 15, 4, NOW() - INTERVAL '1 day'),

('POST', 16, 2, NOW() - INTERVAL '2 days'), ('POST', 16, 3, NOW() - INTERVAL '1 day'),

('POST', 17, 3, NOW() - INTERVAL '1 day'), ('POST', 17, 4, NOW() - INTERVAL '12 hours'),

('POST', 18, 2, NOW() - INTERVAL '12 hours'), ('POST', 18, 3, NOW() - INTERVAL '6 hours'),

('POST', 19, 4, NOW() - INTERVAL '6 hours'),

('POST', 20, 2, NOW() - INTERVAL '3 hours');

-- 총 약 300-400개의 좋아요

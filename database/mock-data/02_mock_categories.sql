-- Mock Categories

INSERT INTO category (id, name, description, display_order, enabled, created_at) VALUES
(1, 'Spring Boot', 'Spring Boot 개발 관련', 1, true, NOW()),
(2, 'React', 'React & Frontend', 2, true, NOW()),
(3, 'Database', '데이터베이스 & SQL', 3, true, NOW()),
(4, 'DevOps', 'CI/CD & 배포', 4, true, NOW()),
(5, '코드리뷰', '코드 리뷰 & 베스트 프랙티스', 5, true, NOW()),
(6, '프로젝트', '프로젝트 경험 공유', 6, true, NOW()),
(7, '질문', '기술 질문 & 답변', 7, true, NOW());

-- Reset sequence
SELECT setval('category_id_seq', (SELECT MAX(id) FROM category));

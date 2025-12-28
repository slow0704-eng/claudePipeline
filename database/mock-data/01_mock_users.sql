-- Mock Users for Board Application
-- Password for all users: "test1234" (BCrypt encoded)

INSERT INTO users (id, username, password, nickname, email, name, role, created_at, enabled) VALUES
(1, 'kimcoder', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '김개발', 'kimcoder@example.com', '김철수', 'MEMBER', NOW() - INTERVAL '6 months', true),
(2, 'leespring', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '이스프링', 'leespring@example.com', '이영희', 'MEMBER', NOW() - INTERVAL '5 months', true),
(3, 'parkjs', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '박자바', 'parkjs@example.com', '박민수', 'MEMBER', NOW() - INTERVAL '4 months', true),
(4, 'choireact', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '최리액트', 'choireact@example.com', '최수진', 'MEMBER', NOW() - INTERVAL '3 months', true),
(5, 'jungdb', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'DB정', 'jungdb@example.com', '정한솔', 'MEMBER', NOW() - INTERVAL '4 months', true),
(6, 'kangdevops', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '강데브옵스', 'kangdevops@example.com', '강지훈', 'MEMBER', NOW() - INTERVAL '2 months', true),
(7, 'ohbackend', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '오백엔드', 'ohbackend@example.com', '오세영', 'MEMBER', NOW() - INTERVAL '5 months', true),
(8, 'yoonfull', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '윤풀스택', 'yoonfull@example.com', '윤서연', 'MEMBER', NOW() - INTERVAL '3 months', true),
(9, 'hancloud', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '한클라우드', 'hancloud@example.com', '한태양', 'MEMBER', NOW() - INTERVAL '1 month', true),
(10, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '관리자', 'admin@example.com', '관리자', 'ADMIN', NOW() - INTERVAL '1 year', true);

-- Reset sequence
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

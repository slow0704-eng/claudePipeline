-- 해시태그 생성 (hashtag)
-- 게시글에 사용된 해시태그 정의

INSERT INTO hashtag (name, created_at) VALUES
('Spring', NOW() - INTERVAL '365 days'),
('Java', NOW() - INTERVAL '365 days'),
('SpringBoot', NOW() - INTERVAL '365 days'),
('JPA', NOW() - INTERVAL '360 days'),
('최적화', NOW() - INTERVAL '355 days'),
('RESTful', NOW() - INTERVAL '350 days'),
('API', NOW() - INTERVAL '350 days'),
('Stream', NOW() - INTERVAL '345 days'),
('Database', NOW() - INTERVAL '340 days'),
('인덱스', NOW() - INTERVAL '335 days'),
('React', NOW() - INTERVAL '330 days'),
('Hooks', NOW() - INTERVAL '325 days'),
('Frontend', NOW() - INTERVAL '320 days'),
('SpringSecurity', NOW() - INTERVAL '315 days'),
('JWT', NOW() - INTERVAL '310 days'),
('인증', NOW() - INTERVAL '305 days'),
('Git', NOW() - INTERVAL '300 days'),
('협업', NOW() - INTERVAL '295 days'),
('Docker', NOW() - INTERVAL '290 days'),
('컨테이너', NOW() - INTERVAL '285 days'),
('TypeScript', NOW() - INTERVAL '280 days'),
('타입', NOW() - INTERVAL '275 days'),
('PostgreSQL', NOW() - INTERVAL '270 days'),
('쿼리', NOW() - INTERVAL '265 days'),
('MSA', NOW() - INTERVAL '260 days'),
('마이크로서비스', NOW() - INTERVAL '255 days'),
('Kubernetes', NOW() - INTERVAL '250 days'),
('배포', NOW() - INTERVAL '245 days'),
('테스트', NOW() - INTERVAL '240 days'),
('TDD', NOW() - INTERVAL '235 days'),
('알고리즘', NOW() - INTERVAL '230 days'),
('자료구조', NOW() - INTERVAL '225 days'),
('코딩테스트', NOW() - INTERVAL '220 days'),
('취업', NOW() - INTERVAL '215 days'),
('면접', NOW() - INTERVAL '210 days'),
('백엔드', NOW() - INTERVAL '205 days'),
('프론트엔드', NOW() - INTERVAL '200 days'),
('풀스택', NOW() - INTERVAL '195 days'),
('DevOps', NOW() - INTERVAL '190 days'),
('CI/CD', NOW() - INTERVAL '185 days'),
('AWS', NOW() - INTERVAL '180 days'),
('클라우드', NOW() - INTERVAL '175 days'),
('MongoDB', NOW() - INTERVAL '170 days'),
('NoSQL', NOW() - INTERVAL '165 days'),
('Redis', NOW() - INTERVAL '160 days'),
('캐시', NOW() - INTERVAL '155 days'),
('성능', NOW() - INTERVAL '150 days'),
('보안', NOW() - INTERVAL '145 days'),
('GraphQL', NOW() - INTERVAL '140 days'),
('WebSocket', NOW() - INTERVAL '135 days'),
('실시간', NOW() - INTERVAL '130 days'),
('Linux', NOW() - INTERVAL '125 days'),
('Shell', NOW() - INTERVAL '120 days'),
('Python', NOW() - INTERVAL '115 days'),
('머신러닝', NOW() - INTERVAL '110 days'),
('AI', NOW() - INTERVAL '105 days'),
('Vue', NOW() - INTERVAL '100 days'),
('Angular', NOW() - INTERVAL '95 days'),
('Next.js', NOW() - INTERVAL '90 days'),
('Nest.js', NOW() - INTERVAL '85 days'),
('Node.js', NOW() - INTERVAL '80 days'),
('Express', NOW() - INTERVAL '75 days'),
('Kafka', NOW() - INTERVAL '70 days'),
('RabbitMQ', NOW() - INTERVAL '65 days'),
('메시지큐', NOW() - INTERVAL '60 days'),
('Elasticsearch', NOW() - INTERVAL '55 days'),
('검색', NOW() - INTERVAL '50 days'),
('모니터링', NOW() - INTERVAL '45 days'),
('로깅', NOW() - INTERVAL '40 days'),
('디버깅', NOW() - INTERVAL '35 days'),
('리팩토링', NOW() - INTERVAL '30 days'),
('클린코드', NOW() - INTERVAL '25 days'),
('디자인패턴', NOW() - INTERVAL '20 days'),
('아키텍처', NOW() - INTERVAL '15 days'),
('DDD', NOW() - INTERVAL '10 days'),
('SOLID', NOW() - INTERVAL '5 days'),
('객체지향', NOW() - INTERVAL '3 days'),
('함수형프로그래밍', NOW() - INTERVAL '1 day');

-- 게시글-해시태그 연결 (board_hashtag)
-- Board ID와 hashtag ID를 연결

INSERT INTO board_hashtag (board_id, hashtag_id) VALUES
-- Board 1: Spring Boot 3.0 새로운 기능 정리 #Spring #Java #SpringBoot
(1, 1),  -- Spring
(1, 2),  -- Java
(1, 3),  -- SpringBoot

-- Board 2: JPA N+1 문제 해결 방법 #JPA #최적화 #Spring
(2, 4),  -- JPA
(2, 5),  -- 최적화
(2, 1),  -- Spring

-- Board 3: RESTful API 설계 가이드 #RESTful #API #백엔드
(3, 6),  -- RESTful
(3, 7),  -- API
(3, 36), -- 백엔드

-- Board 4: Java Stream API 활용법 #Java #Stream #함수형프로그래밍
(4, 2),  -- Java
(4, 8),  -- Stream
(4, 78), -- 함수형프로그래밍

-- Board 5: 데이터베이스 인덱스 최적화 #Database #인덱스 #최적화
(5, 9),  -- Database
(5, 10), -- 인덱스
(5, 5),  -- 최적화

-- Board 6: React Hooks 완벽 가이드 #React #Hooks #Frontend
(6, 11), -- React
(6, 12), -- Hooks
(6, 13), -- Frontend

-- Board 7: Spring Security JWT 인증 구현 #SpringSecurity #JWT #인증
(7, 14), -- SpringSecurity
(7, 15), -- JWT
(7, 16), -- 인증

-- Board 8: Git 협업 워크플로우 #Git #협업
(8, 17), -- Git
(8, 18), -- 협업

-- Board 9: Docker 컨테이너 기초 #Docker #컨테이너 #DevOps
(9, 19), -- Docker
(9, 20), -- 컨테이너
(9, 39), -- DevOps

-- Board 10: TypeScript 기초부터 실전까지 #TypeScript #타입 #Frontend
(10, 21), -- TypeScript
(10, 22), -- 타입
(10, 13), -- Frontend

-- Board 11-20: 다양한 해시태그 조합
(11, 23), -- PostgreSQL
(11, 24), -- 쿼리
(11, 9),  -- Database

(12, 25), -- MSA
(12, 26), -- 마이크로서비스
(12, 1),  -- Spring

(13, 27), -- Kubernetes
(13, 28), -- 배포
(13, 39), -- DevOps

(14, 29), -- 테스트
(14, 30), -- TDD
(14, 2),  -- Java

(15, 31), -- 알고리즘
(15, 32), -- 자료구조
(15, 33), -- 코딩테스트

(16, 34), -- 취업
(16, 35), -- 면접
(16, 36), -- 백엔드

(17, 11), -- React
(17, 37), -- 프론트엔드
(17, 59), -- Next.js

(18, 40), -- CI/CD
(18, 39), -- DevOps
(18, 28), -- 배포

(19, 41), -- AWS
(19, 42), -- 클라우드
(19, 28), -- 배포

(20, 43), -- MongoDB
(20, 44), -- NoSQL
(20, 9),  -- Database

-- 추가 게시글들에도 해시태그 연결
(21, 45), -- Redis
(21, 46), -- 캐시
(21, 47), -- 성능

(22, 14), -- SpringSecurity
(22, 48), -- 보안
(22, 1),  -- Spring

(23, 49), -- GraphQL
(23, 7),  -- API
(23, 36), -- 백엔드

(24, 50), -- WebSocket
(24, 51), -- 실시간
(24, 2),  -- Java

(25, 52), -- Linux
(25, 53), -- Shell
(25, 39), -- DevOps

(26, 54), -- Python
(26, 55), -- 머신러닝
(26, 56), -- AI

(27, 57), -- Vue
(27, 13), -- Frontend
(27, 37), -- 프론트엔드

(28, 58), -- Angular
(28, 13), -- Frontend
(28, 21), -- TypeScript

(29, 59), -- Next.js
(29, 11), -- React
(29, 13), -- Frontend

(30, 60), -- Nest.js
(30, 61), -- Node.js
(30, 36); -- 백엔드

-- 총 78개의 해시태그, 90+개의 연결 관계

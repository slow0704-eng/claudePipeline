-- 북마크 생성 (bookmark)
-- 사용자들이 유용한 게시글을 북마크

INSERT INTO bookmark (user_id, board_id, created_at) VALUES
-- user001 (코딩마스터) - 다양한 기술 관심
(2, 1, NOW() - INTERVAL '29 days'),  -- Spring Boot 3.0
(2, 3, NOW() - INTERVAL '25 days'),  -- RESTful API 설계
(2, 5, NOW() - INTERVAL '21 days'),  -- 데이터베이스 최적화
(2, 10, NOW() - INTERVAL '15 days'), -- DevOps 관련
(2, 15, NOW() - INTERVAL '10 days'), -- 알고리즘
(2, 20, NOW() - INTERVAL '5 days'),  -- 최신 기술

-- user002 (개발왕초보) - 학습 목적 북마크
(3, 1, NOW() - INTERVAL '28 days'),  -- Spring Boot 기초
(3, 2, NOW() - INTERVAL '27 days'),  -- JPA N+1
(3, 4, NOW() - INTERVAL '23 days'),  -- Java 기초
(3, 6, NOW() - INTERVAL '19 days'),  -- React 기초
(3, 8, NOW() - INTERVAL '15 days'),  -- Git 사용법
(3, 12, NOW() - INTERVAL '10 days'), -- 코딩 테스트
(3, 16, NOW() - INTERVAL '6 days'),  -- 취업 준비
(3, 18, NOW() - INTERVAL '3 days'),  -- 면접 준비

-- user003 (JavaLover) - Java 중심
(4, 2, NOW() - INTERVAL '27 days'),  -- JPA
(4, 4, NOW() - INTERVAL '23 days'),  -- Java 성능
(4, 7, NOW() - INTERVAL '17 days'),  -- Spring Security
(4, 11, NOW() - INTERVAL '12 days'), -- Java 디자인 패턴
(4, 14, NOW() - INTERVAL '8 days'),  -- JVM 튜닝

-- user004 (SpringMaster) - Spring 전문
(5, 1, NOW() - INTERVAL '30 days'),  -- Spring Boot 3.0
(5, 3, NOW() - INTERVAL '25 days'),  -- RESTful API
(5, 7, NOW() - INTERVAL '17 days'),  -- Spring Security
(5, 9, NOW() - INTERVAL '13 days'),  -- Spring Batch
(5, 13, NOW() - INTERVAL '9 days'),  -- MSA

-- user005 (ReactNinja) - 프론트엔드
(6, 6, NOW() - INTERVAL '19 days'),  -- React
(6, 10, NOW() - INTERVAL '11 days'), -- Frontend 최적화
(6, 15, NOW() - INTERVAL '7 days'),  -- TypeScript
(6, 19, NOW() - INTERVAL '4 days'),  -- UI/UX

-- user006 (DB전문가) - 데이터베이스
(7, 5, NOW() - INTERVAL '21 days'),  -- DB 최적화
(7, 8, NOW() - INTERVAL '15 days'),  -- PostgreSQL
(7, 12, NOW() - INTERVAL '10 days'), -- Redis
(7, 17, NOW() - INTERVAL '5 days'),  -- MongoDB

-- 다양한 사용자들의 북마크
(8, 1, NOW() - INTERVAL '25 days'),
(8, 2, NOW() - INTERVAL '20 days'),
(8, 3, NOW() - INTERVAL '15 days'),
(8, 4, NOW() - INTERVAL '10 days'),
(8, 5, NOW() - INTERVAL '5 days'),

(9, 1, NOW() - INTERVAL '24 days'),
(9, 6, NOW() - INTERVAL '18 days'),
(9, 11, NOW() - INTERVAL '12 days'),
(9, 16, NOW() - INTERVAL '6 days'),

(10, 2, NOW() - INTERVAL '22 days'),
(10, 7, NOW() - INTERVAL '16 days'),
(10, 12, NOW() - INTERVAL '10 days'),
(10, 17, NOW() - INTERVAL '4 days'),

(11, 3, NOW() - INTERVAL '20 days'),
(11, 8, NOW() - INTERVAL '14 days'),
(11, 13, NOW() - INTERVAL '8 days'),
(11, 18, NOW() - INTERVAL '2 days'),

(12, 4, NOW() - INTERVAL '18 days'),
(12, 9, NOW() - INTERVAL '12 days'),
(12, 14, NOW() - INTERVAL '6 days'),

(13, 5, NOW() - INTERVAL '16 days'),
(13, 10, NOW() - INTERVAL '10 days'),
(13, 15, NOW() - INTERVAL '4 days'),

(14, 1, NOW() - INTERVAL '14 days'),
(14, 6, NOW() - INTERVAL '8 days'),
(14, 11, NOW() - INTERVAL '2 days'),

(15, 2, NOW() - INTERVAL '12 days'),
(15, 7, NOW() - INTERVAL '6 days'),
(15, 12, NOW() - INTERVAL '1 day'),

(16, 3, NOW() - INTERVAL '10 days'),
(16, 8, NOW() - INTERVAL '4 days'),

(17, 4, NOW() - INTERVAL '8 days'),
(17, 9, NOW() - INTERVAL '2 days'),

(18, 5, NOW() - INTERVAL '6 days'),
(18, 10, NOW() - INTERVAL '1 day'),

(19, 1, NOW() - INTERVAL '5 days'),
(19, 6, NOW() - INTERVAL '12 hours'),

(20, 2, NOW() - INTERVAL '4 days'),
(20, 7, NOW() - INTERVAL '6 hours'),

(21, 3, NOW() - INTERVAL '3 days'),
(21, 8, NOW() - INTERVAL '3 hours'),

(22, 4, NOW() - INTERVAL '2 days'),
(22, 9, NOW() - INTERVAL '1 hour'),

(23, 5, NOW() - INTERVAL '1 day'),

(24, 1, NOW() - INTERVAL '20 days'),
(24, 2, NOW() - INTERVAL '15 days'),
(24, 3, NOW() - INTERVAL '10 days'),

(25, 4, NOW() - INTERVAL '18 days'),
(25, 5, NOW() - INTERVAL '12 days'),
(25, 6, NOW() - INTERVAL '6 days'),

(26, 1, NOW() - INTERVAL '16 days'),
(26, 7, NOW() - INTERVAL '10 days'),
(26, 13, NOW() - INTERVAL '4 days'),

(27, 2, NOW() - INTERVAL '14 days'),
(27, 8, NOW() - INTERVAL '8 days'),
(27, 14, NOW() - INTERVAL '2 days'),

(28, 3, NOW() - INTERVAL '12 days'),
(28, 9, NOW() - INTERVAL '6 days'),

(29, 4, NOW() - INTERVAL '10 days'),
(29, 10, NOW() - INTERVAL '4 days'),

(30, 5, NOW() - INTERVAL '8 days'),
(30, 11, NOW() - INTERVAL '2 days'),

(31, 1, NOW() - INTERVAL '7 days'),
(31, 6, NOW() - INTERVAL '1 day'),

(32, 2, NOW() - INTERVAL '6 days'),
(32, 7, NOW() - INTERVAL '12 hours'),

(33, 3, NOW() - INTERVAL '5 days'),
(33, 8, NOW() - INTERVAL '6 hours'),

(34, 4, NOW() - INTERVAL '4 days'),

(35, 5, NOW() - INTERVAL '3 days'),

(36, 1, NOW() - INTERVAL '2 days'),

(37, 2, NOW() - INTERVAL '1 day'),

(38, 3, NOW() - INTERVAL '12 hours'),

(39, 4, NOW() - INTERVAL '6 hours'),

(40, 5, NOW() - INTERVAL '3 hours');

-- 총 약 100개의 북마크

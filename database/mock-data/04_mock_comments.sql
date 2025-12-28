-- Mock Comments (with replies)

INSERT INTO comment (id, board_id, user_id, nickname, content, parent_comment_id, created_at, is_deleted, like_count) VALUES
-- Comments on Post 1 (Spring Boot 3.2)
(1, 1, 3, '박자바', '저도 최근에 업그레이드 했는데 동감입니다! 특히 startup 시간 개선이 체감되더라고요.', NULL, NOW() - INTERVAL '3 days' + INTERVAL '2 hours', false, 5),
(2, 1, 5, 'DB정', 'Hibernate 6.x 변경사항도 체크하셔야 해요. 몇 가지 deprecated 메서드들이 있습니다.', NULL, NOW() - INTERVAL '3 days' + INTERVAL '4 hours', false, 3),
(3, 1, 2, '이스프링', '@DB정 정보 감사합니다! 확인해보겠습니다.', 2, NOW() - INTERVAL '3 days' + INTERVAL '5 hours', false, 1),

-- Comments on Post 2 (JPA N+1)
(4, 2, 4, '최리액트', 'Fetch Join 정말 유용하죠. 저도 애용하고 있습니다!', NULL, NOW() - INTERVAL '5 days' + INTERVAL '1 hour', false, 8),
(5, 2, 7, '오백엔드', 'EntityGraph도 좋지만 Fetch Join이 더 명시적이라 저도 선호합니다.', NULL, NOW() - INTERVAL '5 days' + INTERVAL '3 hours', false, 6),
(6, 2, 1, '김개발', 'BatchSize 설정도 같이 해주면 더 좋아요. spring.jpa.properties.hibernate.default_batch_fetch_size=100', NULL, NOW() - INTERVAL '5 days' + INTERVAL '6 hours', false, 12),
(7, 2, 3, '박자바', '@김개발 좋은 팁 감사합니다! 바로 적용해봐야겠네요.', 6, NOW() - INTERVAL '5 days' + INTERVAL '7 hours', false, 4),

-- Comments on Post 7 (React Query)
(8, 7, 8, '윤풀스택', 'React Query 정말 좋죠! SWR도 괜찮던데 비교해보신 적 있으신가요?', NULL, NOW() - INTERVAL '8 days' + INTERVAL '2 hours', false, 7),
(9, 7, 4, '최리액트', 'SWR도 좋지만 React Query가 기능이 더 많아서 선택했어요. devtools도 편하고요!', 8, NOW() - INTERVAL '8 days' + INTERVAL '3 hours', false, 5),
(10, 7, 2, '이스프링', 'invalidateQueries 기능 정말 편하더라고요. 캐시 관리가 쉬워졌어요.', NULL, NOW() - INTERVAL '8 days' + INTERVAL '5 hours', false, 4),

-- Comments on Post 8 (PostgreSQL vs MySQL)
(11, 8, 3, '박자바', 'JSONB 타입 정말 유용해요. NoSQL처럼 쓸 수 있어서 좋습니다.', NULL, NOW() - INTERVAL '4 days' + INTERVAL '1 hour', false, 9),
(12, 8, 6, '강데브옵스', '운영 관점에서는 PostgreSQL이 더 안정적이라고 느꼈어요.', NULL, NOW() - INTERVAL '4 days' + INTERVAL '2 hours', false, 6),
(13, 8, 7, '오백엔드', 'MySQL 8.0부터는 JSON 타입도 지원해서 차이가 많이 줄었습니다.', NULL, NOW() - INTERVAL '4 days' + INTERVAL '4 hours', false, 4),
(14, 8, 5, 'DB정', '맞아요! 하지만 인덱싱 측면에서는 PostgreSQL JSONB가 더 강력한 것 같아요.', 13, NOW() - INTERVAL '4 days' + INTERVAL '5 hours', false, 7),

-- Comments on Post 9 (Index Design)
(15, 9, 5, 'DB정', '카디널리티 관련해서 정말 공감합니다. 성별같은 컬럼에 인덱스 만들었다가 후회했어요 ㅠㅠ', NULL, NOW() - INTERVAL '9 days' + INTERVAL '1 hour', false, 11),
(16, 9, 1, '김개발', '복합 인덱스 순서 정말 중요하죠. WHERE 조건 순서와 맞춰야 합니다!', NULL, NOW() - INTERVAL '9 days' + INTERVAL '3 hours', false, 8),
(17, 9, 7, '오백엔드', 'EXPLAIN ANALYZE 정말 유용해요. 실제 실행 시간까지 보여줘서 좋습니다.', NULL, NOW() - INTERVAL '9 days' + INTERVAL '5 hours', false, 6),

-- Comments on Post 11 (Docker)
(18, 11, 9, '한클라우드', '멀티 스테이지 빌드 필수죠! 이미지 크기 차이가 엄청나더라고요.', NULL, NOW() - INTERVAL '1 day' + INTERVAL '2 hours', false, 5),
(19, 11, 7, '오백엔드', '.dockerignore 파일도 잘 설정하면 빌드 시간 단축할 수 있어요!', NULL, NOW() - INTERVAL '1 day' + INTERVAL '4 hours', false, 4),
(20, 11, 6, '강데브옵스', '맞습니다! node_modules나 target 디렉토리 제외하는 거 중요해요.', 19, NOW() - INTERVAL '1 day' + INTERVAL '5 hours', false, 3),

-- Comments on Post 12 (GitHub Actions)
(21, 12, 8, '윤풀스택', 'GitHub Actions 무료 티어로 충분한가요? 빌드 시간 제한이 있던데...', NULL, NOW() - INTERVAL '5 days' + INTERVAL '1 hour', false, 6),
(22, 12, 6, '강데브옵스', '월 2000분 제공되는데 개인 프로젝트는 충분해요. 최적화하면 한 빌드당 5분 이내로 가능합니다.', 21, NOW() - INTERVAL '5 days' + INTERVAL '2 hours', false, 8),
(23, 12, 9, '한클라우드', 'actions/cache 사용하면 의존성 캐싱돼서 빌드 시간 많이 단축됩니다!', NULL, NOW() - INTERVAL '5 days' + INTERVAL '4 hours', false, 7),

-- Comments on Post 13 (Kubernetes)
(24, 13, 6, '강데브옵스', 'K8s 학습 곡선이 가파르긴 한데, 배워두면 정말 유용하죠!', NULL, NOW() - INTERVAL '11 days' + INTERVAL '1 hour', false, 10),
(25, 13, 9, '한클라우드', 'Helm 차트도 같이 공부하시면 좋아요. 패키지 관리가 편해집니다.', NULL, NOW() - INTERVAL '11 days' + INTERVAL '3 hours', false, 8),
(26, 13, 7, '오백엔드', '로컬에서 Minikube로 테스트해보는 것도 추천드려요!', NULL, NOW() - INTERVAL '11 days' + INTERVAL '5 hours', false, 6),
(27, 13, 9, '한클라우드', '@오백엔드 Docker Desktop의 K8s도 괜찮더라고요.', 26, NOW() - INTERVAL '11 days' + INTERVAL '6 hours', false, 4),

-- Comments on Post 14 (Clean Code)
(28, 14, 3, '박자바', '변수명 짓기 정말 어렵죠 ㅠㅠ 고민하는 시간이 코딩하는 시간보다 길 때도 있어요.', NULL, NOW() - INTERVAL '6 days' + INTERVAL '2 hours', false, 9),
(29, 14, 8, '윤풀스택', '영어를 잘해야 좋은 변수명을 지을 수 있는 것 같아요. 번역기 많이 돌립니다 ㅎㅎ', NULL, NOW() - INTERVAL '6 days' + INTERVAL '4 hours', false, 7),
(30, 14, 1, '김개발', '일관성이 중요한 것 같아요. 팀 내 네이밍 컨벤션을 정해두면 좋습니다.', NULL, NOW() - INTERVAL '6 days' + INTERVAL '6 hours', false, 5),

-- Comments on Post 16 (Side Project)
(31, 16, 4, '최리액트', '6개월 완성 대단하시네요! 중간에 포기하지 않으신 게 대단합니다.', NULL, NOW() - INTERVAL '2 days' + INTERVAL '1 hour', false, 8),
(32, 16, 1, '김개발', 'GitHub 링크 공유해주실 수 있나요? 참고하고 싶습니다!', NULL, NOW() - INTERVAL '2 days' + INTERVAL '3 hours', false, 6),
(33, 16, 8, '윤풀스택', '테스트 코드는 어느 정도 커버리지로 작성하셨나요?', NULL, NOW() - INTERVAL '2 days' + INTERVAL '5 hours', false, 4),
(34, 16, 8, '윤풀스택', '(질문이 많아 죄송합니다!) 배포 자동화는 어떻게 하셨어요?', NULL, NOW() - INTERVAL '2 days' + INTERVAL '5 hours', false, 3),

-- Comments on Post 17 (MSA)
(35, 17, 7, '오백엔드', 'MSA 전환 프로젝트 경험해보고 싶네요. 서비스 분리 기준이 제일 어려울 것 같아요.', NULL, NOW() - INTERVAL '14 days' + INTERVAL '2 hours', false, 11),
(36, 17, 9, '한클라우드', 'Domain-Driven Design 개념이 도움 많이 됐어요. Bounded Context 단위로 분리했습니다.', NULL, NOW() - INTERVAL '14 days' + INTERVAL '4 hours', false, 9),
(37, 17, 6, '강데브옵스', 'API Gateway는 어떤 걸 사용하셨나요? Spring Cloud Gateway? Kong?', NULL, NOW() - INTERVAL '14 days' + INTERVAL '6 hours', false, 7),
(38, 17, 9, '한클라우드', '@강데브옵스 Spring Cloud Gateway 사용했습니다. K8s Ingress와 조합해서 써봤어요.', 37, NOW() - INTERVAL '14 days' + INTERVAL '7 hours', false, 5),

-- Comments on Post 18 (JWT Storage)
(39, 18, 6, '강데브옵스', 'httpOnly Cookie + CSRF 토큰 조합 추천드립니다. 보안과 UX 둘 다 챙길 수 있어요.', NULL, NOW() - INTERVAL '1 day' + INTERVAL '1 hour', false, 7),
(40, 18, 7, '오백엔드', 'Refresh Token은 httpOnly Cookie, Access Token은 Memory에 저장하는 방식도 있어요.', NULL, NOW() - INTERVAL '1 day' + INTERVAL '2 hours', false, 6),
(41, 18, 2, '이스프링', '좋은 답변들 감사합니다! httpOnly Cookie 방식으로 가야겠네요.', NULL, NOW() - INTERVAL '1 day' + INTERVAL '4 hours', false, 4),

-- Comments on Post 19 (Redis vs Memcached)
(42, 19, 5, 'DB정', '요구사항 보니 Redis 추천드립니다. 데이터 구조가 다양하고 Sorted Set으로 랭킹 구현 가능해요.', NULL, NOW() - INTERVAL '8 days' + INTERVAL '1 hour', false, 9),
(43, 19, 7, '오백엔드', 'Redis Pub/Sub 기능도 유용하고, 영속성 옵션도 있어서 좋습니다.', NULL, NOW() - INTERVAL '8 days' + INTERVAL '2 hours', false, 7),
(44, 19, 3, '박자바', '답변 감사합니다! Redis로 결정했습니다.', NULL, NOW() - INTERVAL '8 days' + INTERVAL '4 hours', false, 3),

-- Comments on Post 20 (WebSocket vs SSE)
(45, 20, 6, '강데브옵스', '단방향이면 SSE가 더 간단하고 효율적입니다. 모바일도 잘 지원되고요.', NULL, NOW() - INTERVAL '3 days' + INTERVAL '1 hour', false, 8),
(46, 20, 8, '윤풀스택', 'SSE는 HTTP 기반이라 방화벽 문제도 없고 구현도 간단해요!', NULL, NOW() - INTERVAL '3 days' + INTERVAL '2 hours', false, 6),
(47, 20, 4, '최리액트', '감사합니다! SSE로 시작해서 나중에 필요하면 WebSocket으로 전환하는 게 좋겠네요.', NULL, NOW() - INTERVAL '3 days' + INTERVAL '4 hours', false, 4),
(48, 20, 9, '한클라우드', 'EventSource API 사용하면 자동 재연결도 되고 편합니다!', NULL, NOW() - INTERVAL '3 days' + INTERVAL '5 hours', false, 5);

-- Update board comment counts
UPDATE board SET comment_count = 3 WHERE id = 1;
UPDATE board SET comment_count = 4 WHERE id = 2;
UPDATE board SET comment_count = 3 WHERE id = 7;
UPDATE board SET comment_count = 4 WHERE id = 8;
UPDATE board SET comment_count = 3 WHERE id = 9;
UPDATE board SET comment_count = 3 WHERE id = 11;
UPDATE board SET comment_count = 3 WHERE id = 12;
UPDATE board SET comment_count = 4 WHERE id = 13;
UPDATE board SET comment_count = 3 WHERE id = 14;
UPDATE board SET comment_count = 4 WHERE id = 16;
UPDATE board SET comment_count = 4 WHERE id = 17;
UPDATE board SET comment_count = 3 WHERE id = 18;
UPDATE board SET comment_count = 3 WHERE id = 19;
UPDATE board SET comment_count = 4 WHERE id = 20;

-- Reset sequence
SELECT setval('comment_id_seq', (SELECT MAX(id) FROM comment));

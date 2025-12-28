-- ================================================================================
-- Complete Mock Data Insertion Script for Board Application
-- Execute this in Render Dashboard Shell
-- ================================================================================

-- ================================================================================
-- 1. USERS (10 developer personas)
-- All passwords: "test1234" (BCrypt encoded)
-- ================================================================================

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

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- ================================================================================
-- 2. CATEGORIES (7 development topics)
-- ================================================================================

INSERT INTO category (id, name, description, display_order, enabled, created_at) VALUES
(1, 'Spring Boot', 'Spring Boot 개발 관련', 1, true, NOW()),
(2, 'React', 'React & Frontend', 2, true, NOW()),
(3, 'Database', '데이터베이스 & SQL', 3, true, NOW()),
(4, 'DevOps', 'CI/CD & 배포', 4, true, NOW()),
(5, '코드리뷰', '코드 리뷰 & 베스트 프랙티스', 5, true, NOW()),
(6, '프로젝트', '프로젝트 경험 공유', 6, true, NOW()),
(7, '질문', '기술 질문 & 답변', 7, true, NOW());

SELECT setval('category_id_seq', (SELECT MAX(id) FROM category));

-- ================================================================================
-- 3. BOARDS (20 development posts)
-- ================================================================================

INSERT INTO board (id, title, content, author, user_id, nickname, category_id, status, created_at, updated_at, view_count, like_count, comment_count, is_draft, is_pinned, is_important) VALUES
-- Spring Boot Posts
(1, 'Spring Boot 3.2 업그레이드 후기',
'최근 프로젝트를 Spring Boot 3.2로 업그레이드했습니다.

주요 변경사항:
1. Java 17 필수
2. Jakarta EE 마이그레이션
3. 성능 개선 체감

특히 startup 시간이 30% 정도 빨라진 것 같습니다.
다만 몇 가지 deprecated API들을 수정해야 했네요.

혹시 업그레이드 하신 분들 경험담 공유해주세요!',
'이스프링', 2, '이스프링', 1, 'PUBLIC', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 156, 23, 0, false, true, true),

(2, 'JPA N+1 문제 해결 방법 정리',
'JPA 사용하면서 성능 이슈가 있어서 정리해봤습니다.

**N+1 문제란?**
연관 관계 조회 시 쿼리가 N+1번 실행되는 문제

**해결 방법:**
1. Fetch Join 사용
```java
@Query("SELECT b FROM Board b JOIN FETCH b.comments")
List<Board> findAllWithComments();
```

2. EntityGraph 사용
3. Batch Size 설정

개인적으로는 Fetch Join이 가장 명확하고 좋은 것 같습니다.',
'박자바', 3, '박자바', 1, 'PUBLIC', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', 234, 45, 0, false, false, true),

(3, 'Spring Security 6 변경사항 요약',
'Spring Boot 3.0으로 넘어오면서 Security도 많이 바뀌었네요.

주요 변경:
- WebSecurityConfigurerAdapter 제거
- Lambda DSL 권장
- authorizeRequests() → authorizeHttpRequests()

마이그레이션 가이드 정리했으니 참고하세요.',
'김개발', 1, '김개발', 1, 'PUBLIC', NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days', 189, 31, 0, false, false, false),

(4, '@Transactional 제대로 이해하기',
'트랜잭션 관련 삽질한 경험 공유합니다.

1. 같은 클래스 내부 호출은 트랜잭션 적용 안됨 (프록시 때문)
2. Checked Exception은 롤백 안됨
3. readOnly=true 옵션 활용

특히 1번 때문에 한참 헤맸네요 ㅠㅠ',
'오백엔드', 7, '오백엔드', 1, 'PUBLIC', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', 178, 28, 0, false, false, false),

-- React Posts
(5, 'React 18 Concurrent Features 정리',
'React 18의 새로운 기능들을 정리해봤습니다.

**주요 기능:**
1. Automatic Batching
2. Transitions
3. Suspense for Data Fetching

성능이 확실히 좋아진 느낌입니다.
특히 렌더링 최적화가 자동으로 되는 게 좋네요.',
'최리액트', 4, '최리액트', 2, 'PUBLIC', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', 142, 19, 0, false, false, false),

(6, 'useState vs useReducer 언제 뭘 쓸까?',
'상태 관리 고민이 많았는데 나름 정리해봤습니다.

**useState 사용:**
- 간단한 상태
- 독립적인 값

**useReducer 사용:**
- 복잡한 상태 로직
- 여러 하위 값을 포함한 객체
- 다음 상태가 이전 상태에 의존

개인적으로는 3개 이상의 관련 상태면 useReducer 쓰는 편입니다.',
'윤풀스택', 8, '윤풀스택', 2, 'PUBLIC', NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days', 167, 22, 0, false, false, false),

(7, 'React Query로 서버 상태 관리 개선하기',
'서버 상태 관리 라이브러리 도입 후기입니다.

**장점:**
- 캐싱 자동화
- 백그라운드 업데이트
- 로딩/에러 상태 관리 간편

코드가 정말 간결해졌어요. 추천합니다!',
'최리액트', 4, '최리액트', 2, 'PUBLIC', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days', 201, 36, 0, false, false, true),

-- Database Posts
(8, 'PostgreSQL vs MySQL 성능 비교',
'실제 프로젝트에서 두 DB를 사용해본 경험을 공유합니다.

**PostgreSQL 장점:**
- 복잡한 쿼리 성능 우수
- JSONB 타입 지원
- 표준 SQL 준수

**MySQL 장점:**
- 읽기 성능 빠름
- 간단한 설정
- 레퍼런스 많음

제 경우는 복잡한 조인이 많아서 PostgreSQL 선택했습니다.',
'DB정', 5, 'DB정', 3, 'PUBLIC', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days', 212, 38, 0, false, false, false),

(9, '인덱스 설계 실수와 교훈',
'인덱스 잘못 설계해서 성능 이슈 겪었던 경험입니다.

**실수:**
- 모든 컬럼에 인덱스 생성 (쓰기 성능 저하)
- 카디널리티 낮은 컬럼에 인덱스
- 복합 인덱스 순서 잘못 설정

**교훈:**
WHERE, JOIN, ORDER BY에 사용되는 컬럼만 인덱스 생성
실행 계획 꼭 확인하기!',
'박자바', 3, '박자바', 3, 'PUBLIC', NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days', 245, 42, 0, false, false, true),

(10, 'SQL 쿼리 최적화 체크리스트',
'느린 쿼리 최적화할 때 체크하는 항목들입니다.

1. EXPLAIN으로 실행 계획 확인
2. 인덱스 사용 여부 확인
3. 불필요한 SELECT * 제거
4. WHERE 조건 최적화
5. JOIN 순서 검토

특히 EXPLAIN은 정말 유용합니다!',
'DB정', 5, 'DB정', 3, 'PUBLIC', NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days', 198, 33, 0, false, false, false),

-- DevOps Posts
(11, 'Docker 멀티 스테이지 빌드로 이미지 크기 줄이기',
'이미지 크기가 1.5GB에서 200MB로 줄었습니다!

```dockerfile
FROM maven:3.9 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

빌드 의존성은 최종 이미지에 포함 안 되니 엄청 가벼워집니다.',
'강데브옵스', 6, '강데브옵스', 4, 'PUBLIC', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 124, 18, 0, false, false, false),

(12, 'GitHub Actions로 CI/CD 파이프라인 구축',
'무료로 CI/CD 구축한 과정 공유합니다.

**파이프라인 구성:**
1. 코드 푸시
2. 테스트 실행
3. Docker 이미지 빌드
4. Container Registry 푸시
5. 서버 배포

GitHub Actions 정말 편하네요. 추천!',
'강데브옵스', 6, '강데브옵스', 4, 'PUBLIC', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', 187, 29, 0, false, false, true),

(13, 'Kubernetes 입문 - 실전 예제',
'K8s 처음 시작하시는 분들께 도움이 되길 바랍니다.

**기본 개념:**
- Pod: 최소 배포 단위
- Service: 네트워크 추상화
- Deployment: 배포 관리

간단한 Spring Boot 앱 배포 예제 포함했습니다.',
'한클라우드', 9, '한클라우드', 4, 'PUBLIC', NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days', 276, 51, 0, false, false, true),

-- Code Review Posts
(14, '클린 코드 - 의미 있는 이름 짓기',
'변수명 고민 많으시죠? 제 나름의 규칙입니다.

**원칙:**
1. 의도가 드러나야 함
2. 검색 가능해야 함
3. 발음 가능해야 함

❌ int d; // elapsed time in days
✅ int elapsedTimeInDays;

코드 리뷰할 때 가장 많이 지적하는 부분이에요.',
'김개발', 1, '김개발', 5, 'PUBLIC', NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days', 165, 27, 0, false, false, false),

(15, '함수는 한 가지만 해야 한다',
'SRP 원칙 적용 사례입니다.

**Before:**
```java
public void processUser(User user) {
    // validate
    // save
    // send email
    // log
}
```

**After:**
```java
public void processUser(User user) {
    validateUser(user);
    saveUser(user);
    sendWelcomeEmail(user);
    logUserCreation(user);
}
```

각 함수가 하나의 책임만 갖도록 분리했습니다.',
'오백엔드', 7, '오백엔드', 5, 'PUBLIC', NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days', 143, 24, 0, false, false, false),

-- Project Posts
(16, '사이드 프로젝트 6개월 회고',
'개인 프로젝트 완성했습니다!

**기술 스택:**
- Backend: Spring Boot, JPA
- Frontend: React, TypeScript
- DB: PostgreSQL
- Deploy: Docker, AWS

**배운 점:**
1. 완성도가 중요하다
2. 문서화의 중요성
3. 테스트 코드 작성 습관

힘들었지만 정말 많이 배웠습니다.',
'윤풀스택', 8, '윤풀스택', 6, 'PUBLIC', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', 198, 35, 0, false, false, true),

(17, 'MSA 전환 프로젝트 후기',
'모놀리식에서 MSA로 전환한 경험입니다.

**도전 과제:**
- 서비스 분리 기준
- 데이터 일관성
- 분산 트랜잭션
- 모니터링

생각보다 어려웠지만 배울 게 많았습니다.',
'한클라우드', 9, '한클라우드', 6, 'PUBLIC', NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days', 267, 48, 0, false, false, true),

-- Question Posts
(18, 'JWT 토큰 저장 위치 고민',
'JWT를 어디에 저장하는 게 좋을까요?

옵션:
1. LocalStorage - XSS 취약
2. Cookie (httpOnly) - CSRF 고려
3. Memory - 새로고침 시 로그아웃

보안과 UX 사이에서 고민입니다. 조언 부탁드립니다!',
'이스프링', 2, '이스프링', 7, 'PUBLIC', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 87, 12, 0, false, false, false),

(19, 'Redis vs Memcached 선택 기준?',
'캐시 솔루션 선택에 고민입니다.

우리 서비스는:
- 세션 저장
- API 응답 캐싱
- 실시간 랭킹

어떤 걸 선택하는 게 좋을까요?',
'박자바', 3, '박자바', 7, 'PUBLIC', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days', 134, 19, 0, false, false, false),

(20, 'WebSocket vs SSE 어떤 걸 써야 할까요?',
'실시간 알림 기능 구현하려고 합니다.

**요구사항:**
- 서버 → 클라이언트 단방향
- 모바일 앱도 지원 예정
- 연결 수: 1만명 정도

WebSocket이 오버스펙일까요? SSE로 충분할까요?',
'최리액트', 4, '최리액트', 7, 'PUBLIC', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 156, 23, 0, false, false, false);

SELECT setval('board_id_seq', (SELECT MAX(id) FROM board));

-- ================================================================================
-- 4. COMMENTS (48 comments with nested replies)
-- ================================================================================

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

SELECT setval('comment_id_seq', (SELECT MAX(id) FROM comment));

-- ================================================================================
-- 5. INTERACTIONS (Likes, Follows, Bookmarks)
-- ================================================================================

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

-- ================================================================================
-- Verify data insertion
-- ================================================================================

SELECT 'Users: ' || COUNT(*) FROM users;
SELECT 'Categories: ' || COUNT(*) FROM category;
SELECT 'Boards: ' || COUNT(*) FROM board;
SELECT 'Comments: ' || COUNT(*) FROM comment;
SELECT 'Likes: ' || COUNT(*) FROM likes;
SELECT 'Follows: ' || COUNT(*) FROM follow;
SELECT 'Bookmarks: ' || COUNT(*) FROM bookmark;

-- ================================================================================
-- END OF SCRIPT
-- ================================================================================

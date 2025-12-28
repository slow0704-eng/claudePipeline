-- Mock Board Posts (Development focused)

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

-- Reset sequence
SELECT setval('board_id_seq', (SELECT MAX(id) FROM board));

# 반응 시스템 마이그레이션 가이드

## 개요

이 가이드는 단일 좋아요 시스템에서 8가지 반응 타입 시스템으로 마이그레이션하는 과정을 안내합니다.

## 마이그레이션 단계

### 1단계: 백업 (필수!)

데이터베이스를 백업합니다. 문제 발생 시 복구할 수 있습니다.

```bash
database\backup_before_reaction_migration.bat
```

또는 수동으로:

```bash
pg_dump -U postgres -d boarddb -F c -b -v -f backup_before_reactions.dump
```

### 2단계: 마이그레이션 실행

Spring Boot 애플리케이션을 **중지한 상태**에서 실행하세요.

```bash
database\run_reaction_migration.bat
```

또는 수동으로:

```bash
psql -U postgres -d boarddb -f database\migrations\add_reaction_types.sql
```

**예상 소요 시간:**
- 소규모 데이터 (< 10,000 레코드): 1-5초
- 중규모 데이터 (< 100,000 레코드): 5-30초
- 대규모 데이터 (> 100,000 레코드): 30초-2분

### 3단계: 데이터 검증

마이그레이션이 성공했는지 확인합니다.

```bash
database\validate_reaction_migration.bat
```

**검증 항목:**
- ✅ `likes` 테이블에 `reaction_type` 컬럼 추가 확인
- ✅ 모든 기존 likes가 `LIKE` 타입으로 설정 확인
- ✅ `board` 테이블에 `reaction_counts` JSONB 컬럼 추가 확인
- ✅ `comments` 테이블에 `reaction_counts` JSONB 컬럼 추가 확인
- ✅ `like_count`와 `reaction_counts['LIKE']` 일치 확인
- ✅ 인덱스 생성 확인

### 4단계: 애플리케이션 재시작

Spring Boot 애플리케이션을 다시 시작합니다.

```bash
# Maven
mvnw spring-boot:run

# Gradle
gradlew bootRun

# JAR 실행
java -jar target/board-0.0.1-SNAPSHOT.jar
```

### 5단계: 기능 테스트

브라우저에서 다음 기능들을 테스트합니다:

#### 게시글 반응 테스트
1. 게시글 상세 페이지 접속
2. 8가지 반응 버튼이 모두 표시되는지 확인
3. 각 반응 버튼 클릭 시:
   - 버튼에 active 스타일 적용 확인
   - 카운트가 1 증가하는지 확인
   - 총 반응 수가 증가하는지 확인
4. 다른 반응 클릭 시:
   - 이전 반응이 취소되고 새 반응이 활성화되는지 확인
   - 카운트가 정확히 업데이트되는지 확인
5. 같은 반응 다시 클릭 시:
   - 반응이 취소되는지 확인
   - 카운트가 감소하는지 확인

#### 댓글 반응 테스트
1. 댓글에 3개의 주요 반응 버튼 표시 확인
2. "+5 더보기" 버튼 클릭 시 나머지 반응 표시 확인
3. 각 반응이 정상 작동하는지 확인

#### 알림 테스트
1. 다른 사용자로 로그인
2. 첫 번째 사용자의 게시글에 반응 추가
3. 첫 번째 사용자가 알림을 받는지 확인
4. 반응 변경 시 중복 알림이 발송되지 않는지 확인

## 마이그레이션 내용

### 데이터베이스 변경사항

#### `likes` 테이블
- **추가:** `reaction_type VARCHAR(20) NOT NULL DEFAULT 'LIKE'`
- **추가:** `updated_at TIMESTAMP`
- **인덱스:** `idx_likes_reaction_type`

#### `board` 테이블
- **추가:** `reaction_counts JSONB`
- **인덱스:** `idx_board_reaction_counts (GIN)`

#### `comments` 테이블
- **추가:** `reaction_counts JSONB`
- **인덱스:** `idx_comments_reaction_counts (GIN)`

### 반응 타입

| 타입 | 이모지 | 한글명 |
|------|--------|--------|
| LIKE | ❤️ | 좋아요 |
| HELPFUL | 👍 | 유익해요 |
| FUNNY | 😂 | 재미있어요 |
| WOW | 😮 | 놀라워요 |
| SAD | 😢 | 슬퍼요 |
| ANGRY | 😡 | 화나요 |
| THINKING | 🤔 | 생각중이에요 |
| CELEBRATE | 🎉 | 축하해요 |

## 롤백 방법

문제 발생 시 백업을 사용하여 롤백할 수 있습니다:

```bash
# 데이터베이스 삭제 및 재생성
dropdb -U postgres boarddb
createdb -U postgres boarddb

# 백업 복원
pg_restore -U postgres -d boarddb -v backup_before_reactions.dump
```

또는 수동 롤백:

```sql
BEGIN;

-- 인덱스 삭제
DROP INDEX IF EXISTS idx_likes_reaction_type;
DROP INDEX IF EXISTS idx_board_reaction_counts;
DROP INDEX IF EXISTS idx_comments_reaction_counts;

-- 컬럼 삭제
ALTER TABLE likes DROP COLUMN IF EXISTS reaction_type;
ALTER TABLE likes DROP COLUMN IF EXISTS updated_at;
ALTER TABLE board DROP COLUMN IF EXISTS reaction_counts;
ALTER TABLE comments DROP COLUMN IF EXISTS reaction_counts;

COMMIT;
```

## 문제 해결

### 오류: "column already exists"

이미 마이그레이션이 실행된 상태입니다. 검증만 실행하세요:

```bash
database\validate_reaction_migration.bat
```

### 오류: "permission denied"

PostgreSQL 사용자에게 충분한 권한이 없습니다:

```sql
GRANT ALL PRIVILEGES ON DATABASE boarddb TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
```

### 오류: "psql: command not found"

PostgreSQL bin 디렉토리를 PATH에 추가하거나 전체 경로를 사용하세요:

```bash
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d boarddb -f database\migrations\add_reaction_types.sql
```

### 데이터 불일치

검증 결과 `like_count`와 `reaction_counts['LIKE']`가 일치하지 않는 경우:

```sql
-- Board 동기화
UPDATE board
SET reaction_counts = jsonb_set(
    reaction_counts,
    '{LIKE}',
    like_count::text::jsonb
)
WHERE like_count != (reaction_counts->>'LIKE')::int;

-- Comments 동기화
UPDATE comments
SET reaction_counts = jsonb_set(
    reaction_counts,
    '{LIKE}',
    like_count::text::jsonb
)
WHERE like_count != (reaction_counts->>'LIKE')::int;
```

## 성능 고려사항

### JSONB 인덱스

GIN 인덱스가 생성되어 JSONB 쿼리 성능이 최적화됩니다:

```sql
-- 인덱스 사용 확인
EXPLAIN ANALYZE
SELECT * FROM board
WHERE reaction_counts->>'LIKE' > '10';
```

### 캐시 워밍

마이그레이션 후 처음 몇 번의 쿼리는 느릴 수 있습니다. 캐시를 워밍업하려면:

```sql
-- 자주 사용되는 쿼리 실행
SELECT id, reaction_counts FROM board LIMIT 100;
SELECT id, reaction_counts FROM comments LIMIT 100;
```

## 모니터링

마이그레이션 후 다음 메트릭을 모니터링하세요:

1. **응답 시간:** 게시글/댓글 로딩 시간
2. **데이터베이스 쿼리 성능:** `reaction_counts` 조회 시간
3. **오류 로그:** 반응 토글 실패 여부
4. **사용자 행동:** 각 반응 타입별 사용 빈도

```sql
-- 반응 타입별 통계
SELECT reaction_type, COUNT(*) as count
FROM likes
GROUP BY reaction_type
ORDER BY count DESC;

-- 가장 많은 반응을 받은 게시글
SELECT
    id,
    title,
    (reaction_counts->>'LIKE')::int +
    (reaction_counts->>'HELPFUL')::int +
    (reaction_counts->>'FUNNY')::int +
    (reaction_counts->>'WOW')::int +
    (reaction_counts->>'SAD')::int +
    (reaction_counts->>'ANGRY')::int +
    (reaction_counts->>'THINKING')::int +
    (reaction_counts->>'CELEBRATE')::int as total_reactions
FROM board
ORDER BY total_reactions DESC
LIMIT 10;
```

## 지원

문제가 발생하면:
1. 로그 파일 확인 (`logs/spring.log`)
2. PostgreSQL 로그 확인
3. 백업에서 복원 고려
4. 개발팀에 문의

---

**마이그레이션 준비 완료!** 위 단계를 차례대로 따라하세요.

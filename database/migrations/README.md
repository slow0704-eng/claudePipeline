# Database Migrations

이 디렉토리에는 데이터베이스 마이그레이션 스크립트가 포함되어 있습니다.

## 마이그레이션 파일 목록

### V001__add_performance_indexes.sql
**목적**: 성능 최적화를 위한 데이터베이스 인덱스 추가

**추가되는 인덱스**:

#### Board 테이블 (6개 인덱스)
- `idx_board_user_id` - 사용자별 게시글 조회
- `idx_board_created_at` - 최신순 정렬
- `idx_board_is_draft` - 임시저장 필터링
- `idx_board_draft_created` - 임시저장 제외 + 최신순 (복합)
- `idx_board_user_created` - 사용자 + 최신순 (복합)

#### Comment 테이블 (4개 인덱스)
- `idx_comment_board_id` - 게시글별 댓글 조회
- `idx_comment_user_id` - 사용자별 댓글 조회
- `idx_comment_is_deleted` - 삭제 여부 필터링
- `idx_comment_board_deleted_created` - 게시글 + 삭제 + 생성일 (복합)

#### Like 테이블 (3개 인덱스)
- `idx_like_user_target` - 좋아요 존재 여부 (복합)
- `idx_like_target` - 특정 대상의 좋아요 수
- `idx_like_reaction_type` - 반응 타입별 조회

#### 기타 테이블 (20+ 인덱스)
- Share, Bookmark, User, Hashtag, Report, Topic 등

**예상 성능 개선**:
- 게시글 목록 조회: 95% 쿼리 수 감소 (N+1 해결)
- 댓글 조회: 3-5배 속도 향상
- 검색 쿼리: 10-20배 속도 향상

## 적용 방법

### 방법 1: psql로 직접 실행
```bash
psql -U postgres -d boarddb -f V001__add_performance_indexes.sql
```

### 방법 2: pgAdmin에서 실행
1. pgAdmin 열기
2. 데이터베이스 선택 (boarddb)
3. Tools > Query Tool
4. 파일 내용 복사 & 붙여넣기
5. 실행 (F5)

### 방법 3: Windows 배치 파일 생성
```batch
@echo off
psql -U postgres -d boarddb -f database\migrations\V001__add_performance_indexes.sql
pause
```

## 인덱스 확인

인덱스가 정상적으로 생성되었는지 확인:

```sql
-- 특정 테이블의 인덱스 확인
SELECT
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'board'
ORDER BY indexname;

-- 모든 테이블의 인덱스 크기 확인
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
ORDER BY pg_relation_size(indexrelid) DESC;
```

## 성능 측정

인덱스 적용 전후 성능 비교:

```sql
-- EXPLAIN ANALYZE로 쿼리 성능 측정
EXPLAIN ANALYZE
SELECT b.*, u.username
FROM board b
LEFT JOIN "user" u ON b.user_id = u.id
WHERE b.is_draft = false
ORDER BY b.created_at DESC
LIMIT 20;
```

**기대 결과**:
- Planning Time: ~0.1ms
- Execution Time: 1-5ms (인덱스 사용 시)
- Execution Time: 50-200ms (인덱스 미사용 시)

## 주의사항

1. **인덱스 크기**: 인덱스는 디스크 공간을 사용합니다. 주기적으로 모니터링하세요.
2. **쓰기 성능**: 인덱스는 INSERT/UPDATE 성능을 약간 저하시킬 수 있습니다. (읽기 성능 향상이 훨씬 큼)
3. **중복 인덱스**: 이미 존재하는 인덱스가 있다면 `IF NOT EXISTS`로 안전하게 처리됩니다.

## 유지보수

### 인덱스 재구축 (필요한 경우)
```sql
-- 특정 인덱스 재구축
REINDEX INDEX idx_board_created_at;

-- 테이블 전체 인덱스 재구축
REINDEX TABLE board;

-- 데이터베이스 전체 인덱스 재구축 (주의!)
REINDEX DATABASE boarddb;
```

### 통계 정보 업데이트
```sql
-- 정기적으로 실행 권장 (주 1회)
ANALYZE board;
ANALYZE comments;
ANALYZE "like";
```

## 롤백

인덱스를 제거하려면:

```sql
-- 예시: Board 테이블의 인덱스 제거
DROP INDEX IF EXISTS idx_board_user_id;
DROP INDEX IF EXISTS idx_board_created_at;
-- 기타 인덱스도 동일하게...
```

## 참고 자료

- [PostgreSQL Index Documentation](https://www.postgresql.org/docs/current/indexes.html)
- [PostgreSQL Performance Tips](https://wiki.postgresql.org/wiki/Performance_Optimization)

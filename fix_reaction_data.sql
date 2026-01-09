-- Quick Fix for Reaction System Data
-- 이 스크립트는 Hibernate 오류를 해결하기 위해 기존 데이터를 업데이트합니다

BEGIN;

-- 1. likes 테이블에 reaction_type 컬럼 추가 (NULL 허용으로 먼저 추가)
ALTER TABLE likes ADD COLUMN IF NOT EXISTS reaction_type VARCHAR(20);

-- 2. 기존 likes를 모두 LIKE 타입으로 설정
UPDATE likes
SET reaction_type = 'LIKE'
WHERE reaction_type IS NULL;

-- 3. NOT NULL 제약 추가
ALTER TABLE likes
ALTER COLUMN reaction_type SET NOT NULL;

-- 4. CHECK 제약 추가 (이미 존재하면 무시)
DO $$
BEGIN
    ALTER TABLE likes
    ADD CONSTRAINT likes_reaction_type_check
    CHECK (reaction_type IN ('LIKE','HELPFUL','FUNNY','WOW','SAD','ANGRY','THINKING','CELEBRATE'));
EXCEPTION
    WHEN duplicate_object THEN
        RAISE NOTICE 'likes_reaction_type_check constraint already exists, skipping';
END $$;

-- 5. updated_at 컬럼 추가
ALTER TABLE likes ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- 6. board reaction_counts 초기화
UPDATE board
SET reaction_counts = jsonb_build_object(
    'LIKE', COALESCE(like_count, 0),
    'HELPFUL', 0,
    'FUNNY', 0,
    'WOW', 0,
    'SAD', 0,
    'ANGRY', 0,
    'THINKING', 0,
    'CELEBRATE', 0
)
WHERE reaction_counts IS NULL;

-- 7. comments reaction_counts 초기화
UPDATE comments
SET reaction_counts = jsonb_build_object(
    'LIKE', COALESCE(like_count, 0),
    'HELPFUL', 0,
    'FUNNY', 0,
    'WOW', 0,
    'SAD', 0,
    'ANGRY', 0,
    'THINKING', 0,
    'CELEBRATE', 0
)
WHERE reaction_counts IS NULL;

-- 8. 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_likes_reaction_type ON likes(reaction_type);
CREATE INDEX IF NOT EXISTS idx_board_reaction_counts ON board USING GIN (reaction_counts);
CREATE INDEX IF NOT EXISTS idx_comments_reaction_counts ON comments USING GIN (reaction_counts);

-- 검증
SELECT
    'likes with NULL reaction_type' as check_name,
    COUNT(*) as count
FROM likes
WHERE reaction_type IS NULL
UNION ALL
SELECT
    'boards with NULL reaction_counts' as check_name,
    COUNT(*) as count
FROM board
WHERE reaction_counts IS NULL
UNION ALL
SELECT
    'comments with NULL reaction_counts' as check_name,
    COUNT(*) as count
FROM comments
WHERE reaction_counts IS NULL;

COMMIT;

\echo ''
\echo '========================================='
\echo '✓ 반응 데이터 수정 완료!'
\echo '이제 Spring Boot를 다시 시작하세요.'
\echo '========================================='

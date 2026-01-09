-- Migration: Add Reaction Types to Support Multiple Reactions
-- Date: 2025-12-29
-- Description: Extends the like system to support 8 different reaction types

BEGIN;

-- 1. likes 테이블에 reaction_type 컬럼 추가
ALTER TABLE likes
ADD COLUMN reaction_type VARCHAR(20) DEFAULT 'LIKE';

-- 2. 기존 데이터를 모두 LIKE로 설정
UPDATE likes
SET reaction_type = 'LIKE'
WHERE reaction_type IS NULL;

-- 3. NOT NULL 제약 추가
ALTER TABLE likes
ALTER COLUMN reaction_type SET NOT NULL;

-- 4. updated_at 컬럼 추가 (반응 변경 추적)
ALTER TABLE likes
ADD COLUMN updated_at TIMESTAMP;

-- 5. board 테이블에 reaction_counts JSONB 추가
ALTER TABLE board
ADD COLUMN reaction_counts JSONB DEFAULT '{"LIKE":0,"HELPFUL":0,"FUNNY":0,"WOW":0,"SAD":0,"ANGRY":0,"THINKING":0,"CELEBRATE":0}'::jsonb;

-- 6. 기존 likeCount를 reaction_counts의 LIKE에 복사
UPDATE board
SET reaction_counts = jsonb_set(
    '{"LIKE":0,"HELPFUL":0,"FUNNY":0,"WOW":0,"SAD":0,"ANGRY":0,"THINKING":0,"CELEBRATE":0}'::jsonb,
    '{LIKE}',
    COALESCE(like_count, 0)::text::jsonb
);

-- 7. comments 테이블에 reaction_counts JSONB 추가
ALTER TABLE comments
ADD COLUMN reaction_counts JSONB DEFAULT '{"LIKE":0,"HELPFUL":0,"FUNNY":0,"WOW":0,"SAD":0,"ANGRY":0,"THINKING":0,"CELEBRATE":0}'::jsonb;

-- 8. 기존 likeCount를 reaction_counts의 LIKE에 복사
UPDATE comments
SET reaction_counts = jsonb_set(
    '{"LIKE":0,"HELPFUL":0,"FUNNY":0,"WOW":0,"SAD":0,"ANGRY":0,"THINKING":0,"CELEBRATE":0}'::jsonb,
    '{LIKE}',
    COALESCE(like_count, 0)::text::jsonb
);

-- 9. 인덱스 생성 (성능 최적화)
CREATE INDEX idx_likes_reaction_type ON likes(reaction_type);
CREATE INDEX idx_board_reaction_counts ON board USING GIN (reaction_counts);
CREATE INDEX idx_comments_reaction_counts ON comments USING GIN (reaction_counts);

-- 10. 데이터 검증
DO $$
DECLARE
    null_count INTEGER;
    mismatch_count INTEGER;
BEGIN
    -- reaction_type이 NULL인 행이 있는지 확인
    SELECT COUNT(*) INTO null_count FROM likes WHERE reaction_type IS NULL;
    IF null_count > 0 THEN
        RAISE EXCEPTION '% likes rows have NULL reaction_type', null_count;
    END IF;

    -- Board의 like_count와 reaction_counts[LIKE]가 일치하는지 확인
    SELECT COUNT(*) INTO mismatch_count
    FROM board
    WHERE like_count != (reaction_counts->>'LIKE')::int;
    IF mismatch_count > 0 THEN
        RAISE WARNING '% board rows have mismatched like_count and reaction_counts[LIKE]', mismatch_count;
    END IF;

    -- Comment의 like_count와 reaction_counts[LIKE]가 일치하는지 확인
    SELECT COUNT(*) INTO mismatch_count
    FROM comments
    WHERE like_count != (reaction_counts->>'LIKE')::int;
    IF mismatch_count > 0 THEN
        RAISE WARNING '% comment rows have mismatched like_count and reaction_counts[LIKE]', mismatch_count;
    END IF;

    RAISE NOTICE 'Migration completed successfully!';
END $$;

COMMIT;

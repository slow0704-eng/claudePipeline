-- Validation Queries for Reaction Migration
-- Date: 2025-12-30

\echo '========================================'
\echo 'Reaction Migration Validation'
\echo '========================================'
\echo ''

-- 1. Check likes table structure
\echo '1. Checking likes table columns...'
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'likes'
  AND column_name IN ('reaction_type', 'updated_at')
ORDER BY column_name;
\echo ''

-- 2. Check reaction_type distribution
\echo '2. Reaction type distribution in likes table:'
SELECT reaction_type, COUNT(*) as count
FROM likes
GROUP BY reaction_type
ORDER BY count DESC;
\echo ''

-- 3. Check for NULL reaction_types (should be 0)
\echo '3. Checking for NULL reaction_types (should be 0):'
SELECT COUNT(*) as null_reaction_count
FROM likes
WHERE reaction_type IS NULL;
\echo ''

-- 4. Check board table columns
\echo '4. Checking board table reaction_counts column...'
SELECT column_name, data_type, column_default
FROM information_schema.columns
WHERE table_name = 'board'
  AND column_name = 'reaction_counts';
\echo ''

-- 5. Sample board reaction_counts
\echo '5. Sample board reaction_counts (first 5 rows):'
SELECT id, title, like_count, reaction_counts
FROM board
ORDER BY id
LIMIT 5;
\echo ''

-- 6. Check like_count vs reaction_counts[LIKE] consistency
\echo '6. Checking board like_count consistency with reaction_counts[LIKE]:'
SELECT
    COUNT(*) as total_rows,
    SUM(CASE WHEN like_count = (reaction_counts->>'LIKE')::int THEN 1 ELSE 0 END) as matching_rows,
    SUM(CASE WHEN like_count != (reaction_counts->>'LIKE')::int THEN 1 ELSE 0 END) as mismatched_rows
FROM board;
\echo ''

-- 7. Show any mismatched rows
\echo '7. Board rows with mismatched like_count (if any):'
SELECT id, title, like_count, (reaction_counts->>'LIKE')::int as reaction_like_count
FROM board
WHERE like_count != (reaction_counts->>'LIKE')::int
LIMIT 10;
\echo ''

-- 8. Check comments table columns
\echo '8. Checking comments table reaction_counts column...'
SELECT column_name, data_type, column_default
FROM information_schema.columns
WHERE table_name = 'comments'
  AND column_name = 'reaction_counts';
\echo ''

-- 9. Sample comment reaction_counts
\echo '9. Sample comment reaction_counts (first 5 rows):'
SELECT id, content, like_count, reaction_counts
FROM comments
ORDER BY id
LIMIT 5;
\echo ''

-- 10. Check comment like_count vs reaction_counts[LIKE] consistency
\echo '10. Checking comment like_count consistency with reaction_counts[LIKE]:'
SELECT
    COUNT(*) as total_rows,
    SUM(CASE WHEN like_count = (reaction_counts->>'LIKE')::int THEN 1 ELSE 0 END) as matching_rows,
    SUM(CASE WHEN like_count != (reaction_counts->>'LIKE')::int THEN 1 ELSE 0 END) as mismatched_rows
FROM comments;
\echo ''

-- 11. Check indexes
\echo '11. Checking reaction-related indexes:'
SELECT indexname, tablename, indexdef
FROM pg_indexes
WHERE indexname IN ('idx_likes_reaction_type', 'idx_board_reaction_counts', 'idx_comments_reaction_counts')
ORDER BY tablename, indexname;
\echo ''

-- 12. Summary statistics
\echo '12. Summary Statistics:'
\echo 'Total likes:'
SELECT COUNT(*) as total_likes FROM likes;
\echo ''
\echo 'Total boards with reactions:'
SELECT COUNT(*) as total_boards FROM board WHERE reaction_counts IS NOT NULL;
\echo ''
\echo 'Total comments with reactions:'
SELECT COUNT(*) as total_comments FROM comments WHERE reaction_counts IS NOT NULL;
\echo ''

\echo '========================================'
\echo 'Validation Complete!'
\echo '========================================'

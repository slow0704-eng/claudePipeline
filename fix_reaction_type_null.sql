-- Fix existing NULL reaction_type values in likes table
-- Run this script manually before deploying the updated application

-- Update all NULL reaction_type to LIKE (default)
UPDATE likes
SET reaction_type = 'LIKE'
WHERE reaction_type IS NULL;

-- Verify the update
SELECT
    COUNT(*) as total_likes,
    SUM(CASE WHEN reaction_type IS NULL THEN 1 ELSE 0 END) as null_count,
    SUM(CASE WHEN reaction_type = 'LIKE' THEN 1 ELSE 0 END) as like_count
FROM likes;

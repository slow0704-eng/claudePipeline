-- 기존 목 데이터 삭제
-- 외래 키 관계를 고려하여 역순으로 삭제

-- 1. 게시글-해시태그 연결 삭제
TRUNCATE TABLE board_hashtag CASCADE;

-- 2. 해시태그 삭제
TRUNCATE TABLE hashtag CASCADE;

-- 3. 댓글 삭제
TRUNCATE TABLE comment CASCADE;

-- 4. 북마크 삭제
TRUNCATE TABLE bookmark CASCADE;

-- 5. 게시글 좋아요 삭제
TRUNCATE TABLE board_like CASCADE;

-- 6. 팔로우 관계 삭제
TRUNCATE TABLE user_follow CASCADE;

-- 7. 게시글 삭제 (첨부파일 연결도 삭제됨)
TRUNCATE TABLE attachment CASCADE;
TRUNCATE TABLE board CASCADE;

-- 8. 사용자 삭제 (admin 제외하고 목 사용자만 삭제)
-- admin은 유지하고 user001~user050만 삭제
DELETE FROM users WHERE username LIKE 'user%';

-- 또는 모든 사용자를 삭제하려면 (admin 포함):
-- TRUNCATE TABLE users CASCADE;

-- 시퀀스 리셋 (선택사항 - ID를 1부터 다시 시작하려면)
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE board_id_seq RESTART WITH 1;
-- ALTER SEQUENCE comment_id_seq RESTART WITH 1;
-- ALTER SEQUENCE hashtag_id_seq RESTART WITH 1;

SELECT 'All mock data cleared successfully!' AS result;

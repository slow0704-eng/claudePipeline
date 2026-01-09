-- 기존 인덱스 삭제 (충돌 해결)
-- user_hashtag_follow 테이블의 중복 인덱스 제거

-- 기존 인덱스 삭제
DROP INDEX IF EXISTS idx_user_id;
DROP INDEX IF EXISTS idx_hashtag_id;

-- 새 인덱스는 Hibernate가 자동 생성
-- idx_uhf_user_id
-- idx_uhf_hashtag_id

SELECT 'Index conflict resolved. Please restart the application.' AS result;

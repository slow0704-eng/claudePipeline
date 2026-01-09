-- 해시태그 버그 진단 및 수정

-- 1. 24번 글의 현재 해시태그 확인
SELECT bh.id, bh.board_id, bh.hashtag_id, h.name, bh.created_at
FROM board_hashtag bh
JOIN hashtag h ON bh.hashtag_id = h.id
WHERE bh.board_id = 24;

-- 2. 31번 글의 현재 해시태그 확인
SELECT bh.id, bh.board_id, bh.hashtag_id, h.name, bh.created_at
FROM board_hashtag bh
JOIN hashtag h ON bh.hashtag_id = h.id
WHERE bh.board_id = 31;

-- 3. 24번 글의 실제 내용 확인 (해시태그가 있는지)
SELECT id, title, content
FROM board
WHERE id = 24;

-- 4. 31번 글의 실제 내용 확인
SELECT id, title, content
FROM board
WHERE id = 31;

-- 5. '해시태그'를 사용하는 모든 게시글 확인
SELECT bh.board_id, b.title, h.name
FROM board_hashtag bh
JOIN hashtag h ON bh.hashtag_id = h.id
JOIN board b ON bh.board_id = b.id
WHERE h.name = '해시태그'
ORDER BY bh.board_id;

-- ===== 수정 SQL (아래는 확인 후 필요시 실행) =====

-- 24번 글에서 '해시태그' 태그 제거 (본문에 없다면)
-- DELETE FROM board_hashtag
-- WHERE board_id = 24
-- AND hashtag_id = (SELECT id FROM hashtag WHERE name = '해시태그');

-- 해시태그 사용 횟수 재계산
-- UPDATE hashtag
-- SET use_count = (
--     SELECT COUNT(*)
--     FROM board_hashtag
--     WHERE hashtag_id = hashtag.id
-- );

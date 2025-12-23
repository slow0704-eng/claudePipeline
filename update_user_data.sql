-- 깨진 닉네임 데이터 수정 SQL

-- 옵션 1: 특정 사용자의 닉네임을 직접 수정
-- user id가 1인 사용자의 닉네임을 '테스트유저'로 수정
UPDATE users
SET nickname = '테스트유저'
WHERE id = 1;

-- 옵션 2: 모든 사용자 정보 확인
SELECT id, username, nickname, email
FROM users;

-- 수정 후 확인
SELECT id, username, nickname, email
FROM users
WHERE id = 1;

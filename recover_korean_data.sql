-- 깨진 한글 데이터 복구 SQL 스크립트

-- 1. 먼저 현재 모든 사용자 데이터 확인
SELECT id, username, nickname, email, name
FROM users
ORDER BY id;

-- 2. 옵션 1: Latin1으로 잘못 저장된 UTF-8 데이터를 복구하는 방법
-- (이 방법은 데이터가 Latin1으로 저장되었다가 다시 UTF-8로 읽혀서 깨진 경우에 작동합니다)
UPDATE users
SET
    nickname = CONVERT(CAST(CONVERT(nickname USING latin1) AS BINARY) USING utf8mb4),
    name = CONVERT(CAST(CONVERT(name USING latin1) AS BINARY) USING utf8mb4)
WHERE nickname REGEXP '[^\x00-\x7F]' OR name REGEXP '[^\x00-\x7F]';

-- 3. 복구 후 확인
SELECT id, username, nickname, email, name
FROM users
ORDER BY id;

-- 4. 만약 위 방법으로 복구가 안 되면, 각 사용자별로 직접 입력
-- 예시: UPDATE users SET nickname = '올바른닉네임', name = '올바른이름' WHERE id = 1;

-- 테스트유저의 경우:
-- UPDATE users SET nickname = '테스트유저' WHERE id = 1;

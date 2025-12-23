-- MySQL 한글 깨짐 문제 해결을 위한 SQL 스크립트

-- 1. 데이터베이스 character set을 UTF-8로 변경
ALTER DATABASE boarddb CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 2. users 테이블의 character set 변경
ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 문자열 컬럼들을 명시적으로 UTF-8로 변경
ALTER TABLE users
  MODIFY username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  MODIFY password VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  MODIFY nickname VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  MODIFY email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  MODIFY name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. 현재 설정 확인
SELECT default_character_set_name, default_collation_name
FROM information_schema.SCHEMATA
WHERE schema_name = 'boarddb';

SHOW CREATE TABLE users;

-- 콘텐츠 관리 강화 기능을 위한 데이터베이스 스키마 업데이트

-- 1. Board 테이블에 새 컬럼 추가
ALTER TABLE board ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PUBLIC' AFTER is_draft;
ALTER TABLE board ADD COLUMN IF NOT EXISTS is_pinned BOOLEAN DEFAULT FALSE AFTER status;
ALTER TABLE board ADD COLUMN IF NOT EXISTS is_important BOOLEAN DEFAULT FALSE AFTER is_pinned;
ALTER TABLE board ADD COLUMN IF NOT EXISTS pinned_until DATETIME AFTER is_important;

-- 2. Board 테이블 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_board_status ON board(status);
CREATE INDEX IF NOT EXISTS idx_board_pinned ON board(is_pinned, pinned_until);

-- 3. 금지어 테이블 생성
CREATE TABLE IF NOT EXISTS banned_words (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    is_regex BOOLEAN NOT NULL DEFAULT FALSE,
    action VARCHAR(20) NOT NULL DEFAULT 'BLOCK',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_banned_word_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 기존 게시글들의 status를 PUBLIC으로 설정
UPDATE board SET status = 'PUBLIC' WHERE status IS NULL OR status = '';

-- 5. 기존 게시글들의 isPinned, isImportant를 FALSE로 설정
UPDATE board SET is_pinned = FALSE WHERE is_pinned IS NULL;
UPDATE board SET is_important = FALSE WHERE is_important IS NULL;

-- 완료
SELECT '스키마 업데이트가 완료되었습니다!' AS message;

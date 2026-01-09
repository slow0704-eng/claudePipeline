-- ==========================================
-- Performance Optimization - Database Indexes
-- 성능 최적화를 위한 데이터베이스 인덱스 추가
-- ==========================================

-- Board 테이블 인덱스
-- user_id: 사용자별 게시글 조회 성능 향상
CREATE INDEX IF NOT EXISTS idx_board_user_id ON board(user_id);

-- created_at: 최신순 정렬 성능 향상
CREATE INDEX IF NOT EXISTS idx_board_created_at ON board(created_at DESC);

-- is_draft: 임시저장 필터링 성능 향상
CREATE INDEX IF NOT EXISTS idx_board_is_draft ON board(is_draft);

-- 복합 인덱스: 임시저장 제외 + 최신순 (가장 자주 사용되는 쿼리)
CREATE INDEX IF NOT EXISTS idx_board_draft_created ON board(is_draft, created_at DESC);

-- 복합 인덱스: 사용자 + 최신순
CREATE INDEX IF NOT EXISTS idx_board_user_created ON board(user_id, created_at DESC);

-- Comment 테이블 인덱스
-- board_id: 게시글별 댓글 조회 성능 향상
CREATE INDEX IF NOT EXISTS idx_comment_board_id ON comments(board_id);

-- user_id: 사용자별 댓글 조회
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON comments(user_id);

-- is_deleted: 삭제되지 않은 댓글 필터링
CREATE INDEX IF NOT EXISTS idx_comment_is_deleted ON comments(is_deleted);

-- 복합 인덱스: 게시글 + 삭제 여부 + 생성일 (댓글 목록 조회)
CREATE INDEX IF NOT EXISTS idx_comment_board_deleted_created ON comments(board_id, is_deleted, created_at ASC);

-- Like 테이블 인덱스
-- user_id + target_type + target_id: 좋아요 존재 여부 확인 (복합 인덱스)
CREATE INDEX IF NOT EXISTS idx_like_user_target ON "like"(user_id, target_type, target_id);

-- target_type + target_id: 특정 대상의 좋아요 수 조회
CREATE INDEX IF NOT EXISTS idx_like_target ON "like"(target_type, target_id);

-- reaction_type: 반응 타입별 조회
CREATE INDEX IF NOT EXISTS idx_like_reaction_type ON "like"(reaction_type);

-- Share 테이블 인덱스
-- board_id: 게시글별 공유 조회
CREATE INDEX IF NOT EXISTS idx_share_board_id ON share(board_id);

-- user_id: 사용자별 공유 조회
CREATE INDEX IF NOT EXISTS idx_share_user_id ON share(user_id);

-- created_at: 최신 공유 순 정렬
CREATE INDEX IF NOT EXISTS idx_share_created_at ON share(created_at DESC);

-- Bookmark 테이블 인덱스
-- user_id + board_id: 북마크 존재 여부 확인 (복합 인덱스)
CREATE INDEX IF NOT EXISTS idx_bookmark_user_board ON bookmark(user_id, board_id);

-- user_id + created_at: 사용자별 북마크 목록 (최신순)
CREATE INDEX IF NOT EXISTS idx_bookmark_user_created ON bookmark(user_id, created_at DESC);

-- User 테이블 인덱스
-- username: 로그인 성능 향상 (이미 UNIQUE 제약조건이 있을 수 있음)
CREATE INDEX IF NOT EXISTS idx_user_username ON "user"(username);

-- email: 이메일 조회 성능 향상
CREATE INDEX IF NOT EXISTS idx_user_email ON "user"(email);

-- Hashtag 테이블 인덱스
-- name: 해시태그 검색 성능 향상 (UNIQUE 제약조건이 있을 수 있음)
CREATE INDEX IF NOT EXISTS idx_hashtag_name ON hashtag(name);

-- UserHashtagFollow 테이블 인덱스 (있는 경우)
-- user_id: 사용자가 팔로우한 해시태그 조회
CREATE INDEX IF NOT EXISTS idx_user_hashtag_follow_user ON user_hashtag_follow(user_id);

-- hashtag_id: 해시태그를 팔로우한 사용자 조회
CREATE INDEX IF NOT EXISTS idx_user_hashtag_follow_hashtag ON user_hashtag_follow(hashtag_id);

-- Report 테이블 인덱스
-- target_type + target_id: 특정 대상의 신고 조회
CREATE INDEX IF NOT EXISTS idx_report_target ON report(target_type, target_id);

-- user_id: 사용자별 신고 조회
CREATE INDEX IF NOT EXISTS idx_report_user_id ON report(user_id);

-- status: 신고 상태별 조회 (처리 대기, 완료 등)
CREATE INDEX IF NOT EXISTS idx_report_status ON report(status);

-- created_at: 최신 신고 순 정렬
CREATE INDEX IF NOT EXISTS idx_report_created_at ON report(created_at DESC);

-- Topic 테이블 인덱스 (있는 경우)
-- name: 토픽 이름 검색
CREATE INDEX IF NOT EXISTS idx_topic_name ON topic(name);

-- BoardTopic 테이블 인덱스 (있는 경우)
-- board_id: 게시글의 토픽 조회
CREATE INDEX IF NOT EXISTS idx_board_topic_board ON board_topic(board_id);

-- topic_id: 토픽의 게시글 조회
CREATE INDEX IF NOT EXISTS idx_board_topic_topic ON board_topic(topic_id);

-- UserTopicFollow 테이블 인덱스 (있는 경우)
-- user_id: 사용자가 팔로우한 토픽 조회
CREATE INDEX IF NOT EXISTS idx_user_topic_follow_user ON user_topic_follow(user_id);

-- topic_id: 토픽을 팔로우한 사용자 조회
CREATE INDEX IF NOT EXISTS idx_user_topic_follow_topic ON user_topic_follow(topic_id);

-- ==========================================
-- 인덱스 적용 후 통계 정보 업데이트
-- ==========================================
ANALYZE board;
ANALYZE comments;
ANALYZE "like";
ANALYZE share;
ANALYZE bookmark;
ANALYZE "user";
ANALYZE hashtag;
ANALYZE report;

-- ==========================================
-- 성공 메시지
-- ==========================================
DO $$
BEGIN
    RAISE NOTICE 'Performance indexes created successfully!';
    RAISE NOTICE 'Run EXPLAIN ANALYZE on your queries to verify performance improvements.';
END $$;

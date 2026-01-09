package com.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 디버그용 컨트롤러
 * DB 연결 상태 및 데이터 확인
 */
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    /**
     * 현재 DB 연결 정보 및 데이터 개수 확인
     */
    @GetMapping("/db-info")
    public Map<String, Object> getDbInfo() {
        Map<String, Object> info = new HashMap<>();

        // DB 연결 정보
        info.put("datasource_url", datasourceUrl);
        info.put("datasource_username", datasourceUsername);

        // 현재 연결된 DB 이름
        String currentDatabase = jdbcTemplate.queryForObject(
            "SELECT current_database()", String.class);
        info.put("current_database", currentDatabase);

        // PostgreSQL 버전
        String version = jdbcTemplate.queryForObject(
            "SELECT version()", String.class);
        info.put("postgresql_version", version);

        // 데이터 개수
        Map<String, Long> counts = new HashMap<>();

        try {
            counts.put("users", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users", Long.class));
        } catch (Exception e) {
            counts.put("users", -1L);
        }

        try {
            counts.put("board", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM board", Long.class));
        } catch (Exception e) {
            counts.put("board", -1L);
        }

        try {
            counts.put("comment", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comment", Long.class));
        } catch (Exception e) {
            counts.put("comment", -1L);
        }

        try {
            counts.put("board_like", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM board_like", Long.class));
        } catch (Exception e) {
            counts.put("board_like", -1L);
        }

        try {
            counts.put("bookmark", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookmark", Long.class));
        } catch (Exception e) {
            counts.put("bookmark", -1L);
        }

        try {
            counts.put("user_follow", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_follow", Long.class));
        } catch (Exception e) {
            counts.put("user_follow", -1L);
        }

        try {
            counts.put("hashtag", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM hashtag", Long.class));
        } catch (Exception e) {
            counts.put("hashtag", -1L);
        }

        info.put("table_counts", counts);

        // 최근 사용자 5명
        try {
            var recentUsers = jdbcTemplate.queryForList(
                "SELECT id, username, nickname, created_at FROM users ORDER BY id DESC LIMIT 5");
            info.put("recent_users", recentUsers);
        } catch (Exception e) {
            info.put("recent_users", "Error: " + e.getMessage());
        }

        // 최근 게시글 5개
        try {
            var recentBoards = jdbcTemplate.queryForList(
                "SELECT id, title, view_count, like_count, created_at FROM board ORDER BY id DESC LIMIT 5");
            info.put("recent_boards", recentBoards);
        } catch (Exception e) {
            info.put("recent_boards", "Error: " + e.getMessage());
        }

        return info;
    }

    /**
     * admin 계정 확인
     */
    @GetMapping("/check-admin")
    public Map<String, Object> checkAdmin() {
        Map<String, Object> result = new HashMap<>();

        try {
            var adminUser = jdbcTemplate.queryForList(
                "SELECT id, username, nickname, role, created_at FROM users WHERE username = 'admin'");
            result.put("admin_exists", !adminUser.isEmpty());
            result.put("admin_info", adminUser);
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * user001 계정 확인 (목 데이터)
     */
    @GetMapping("/check-mock-data")
    public Map<String, Object> checkMockData() {
        Map<String, Object> result = new HashMap<>();

        try {
            var user001 = jdbcTemplate.queryForList(
                "SELECT id, username, nickname, role, created_at FROM users WHERE username = 'user001'");
            result.put("user001_exists", !user001.isEmpty());
            result.put("user001_info", user001);

            // user001~user050 개수 확인
            Long mockUserCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username LIKE 'user%'", Long.class);
            result.put("mock_user_count", mockUserCount);
            result.put("expected_mock_users", 50);
            result.put("mock_data_inserted", mockUserCount >= 50);

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * 모든 게시글의 해시태그를 재동기화
     * 본문에서 해시태그를 다시 추출하여 DB에 반영
     */
    @GetMapping("/resync-all-hashtags")
    public String resyncAllHashtags() {
        try {
            // 모든 게시글 조회
            var boards = jdbcTemplate.queryForList(
                "SELECT id, title, content, is_draft FROM board ORDER BY id");

            int totalBoards = boards.size();
            int updatedCount = 0;
            StringBuilder log = new StringBuilder();
            log.append("=== 해시태그 재동기화 시작 ===\n");
            log.append("총 게시글 수: ").append(totalBoards).append("\n\n");

            for (var board : boards) {
                Long boardId = ((Number) board.get("id")).longValue();
                String title = (String) board.get("title");
                String content = (String) board.get("content");
                Boolean isDraft = (Boolean) board.get("is_draft");

                if (Boolean.TRUE.equals(isDraft)) {
                    log.append("게시글 #").append(boardId).append(" (").append(title).append(") - 임시저장 건너뜀\n");
                    continue;
                }

                // 기존 해시태그 삭제
                int deletedCount = jdbcTemplate.update(
                    "DELETE FROM board_hashtag WHERE board_id = ?", boardId);

                // 본문에서 해시태그 추출 (정규식)
                String fullText = title + " " + content;
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("#([가-힣a-zA-Z0-9_]+)");
                java.util.regex.Matcher matcher = pattern.matcher(fullText);
                java.util.Set<String> extractedTags = new java.util.HashSet<>();

                while (matcher.find()) {
                    String tag = matcher.group(1).toLowerCase();
                    if (tag.length() <= 50) {
                        extractedTags.add(tag);
                    }
                }

                log.append("게시글 #").append(boardId).append(" (").append(title).append(")\n");
                log.append("  - 삭제된 해시태그: ").append(deletedCount).append("개\n");
                log.append("  - 추출된 해시태그: ").append(extractedTags).append("\n");

                // 새 해시태그 연결
                for (String tagName : extractedTags) {
                    // 해시태그 찾기 또는 생성
                    var existingHashtag = jdbcTemplate.queryForList(
                        "SELECT id FROM hashtag WHERE name = ?", tagName);

                    Long hashtagId;
                    if (existingHashtag.isEmpty()) {
                        // 새 해시태그 생성
                        jdbcTemplate.update(
                            "INSERT INTO hashtag (name, use_count, last_used_at, created_at, is_banned) " +
                            "VALUES (?, 1, NOW(), NOW(), false)", tagName);
                        hashtagId = jdbcTemplate.queryForObject(
                            "SELECT id FROM hashtag WHERE name = ?", Long.class, tagName);
                    } else {
                        hashtagId = ((Number) existingHashtag.get(0).get("id")).longValue();
                        // 사용 횟수 증가
                        jdbcTemplate.update(
                            "UPDATE hashtag SET use_count = use_count + 1, last_used_at = NOW() WHERE id = ?",
                            hashtagId);
                    }

                    // 게시글-해시태그 연결
                    jdbcTemplate.update(
                        "INSERT INTO board_hashtag (board_id, hashtag_id, created_at) VALUES (?, ?, NOW())",
                        boardId, hashtagId);

                    log.append("  - 연결: #").append(tagName).append(" (ID: ").append(hashtagId).append(")\n");
                }

                log.append("\n");
                updatedCount++;
            }

            // 사용되지 않는 해시태그의 use_count 재계산
            jdbcTemplate.update(
                "UPDATE hashtag SET use_count = (" +
                "  SELECT COUNT(*) FROM board_hashtag WHERE hashtag_id = hashtag.id" +
                ")");

            log.append("=== 재동기화 완료 ===\n");
            log.append("처리된 게시글: ").append(updatedCount).append("/").append(totalBoards).append("\n");

            return log.toString();

        } catch (Exception e) {
            return "에러 발생: " + e.getMessage() + "\n" + java.util.Arrays.toString(e.getStackTrace());
        }
    }
}

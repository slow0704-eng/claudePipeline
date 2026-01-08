package com.board.controller;

import com.board.util.ApiResponse;
import com.board.entity.Board;
import com.board.entity.Hashtag;
import com.board.entity.User;
import com.board.service.BoardService;
import com.board.service.HashtagService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import com.board.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;
    private final BoardService boardService;
    private final UserService userService;

    /**
     * 해시태그 자동완성 API
     * GET /api/hashtags/search?q=java
     */
    @GetMapping("/api/hashtags/search")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchHashtags(@RequestParam String q) {
        List<Map<String, Object>> hashtags = hashtagService.searchHashtags(q);
        Map<String, Object> data = Map.of(
                "hashtags", hashtags,
                "count", hashtags.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 인기 해시태그 조회 API
     * GET /api/hashtags/popular?limit=20
     */
    @GetMapping("/api/hashtags/popular")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPopularHashtags(
            @RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> hashtags = hashtagService.getPopularHashtags(limit);
        Map<String, Object> data = Map.of(
                "hashtags", hashtags,
                "count", hashtags.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 트렌딩 해시태그 조회 API (최근 24시간)
     * GET /api/hashtags/trending?limit=10
     */
    @GetMapping("/api/hashtags/trending")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrendingHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> hashtags = hashtagService.getTrendingHashtags(limit);
        Map<String, Object> data = Map.of(
                "hashtags", hashtags,
                "count", hashtags.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 최근 사용된 해시태그 조회 API
     * GET /api/hashtags/recent?limit=10
     */
    @GetMapping("/api/hashtags/recent")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecentHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> hashtags = hashtagService.getRecentHashtags(limit);
        Map<String, Object> data = Map.of(
                "hashtags", hashtags,
                "count", hashtags.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 특정 해시태그 통계 조회 API
     * GET /api/hashtags/stats?name=java
     */
    @GetMapping("/api/hashtags/stats")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHashtagStats(@RequestParam String name) {
        Map<String, Object> stats = hashtagService.getHashtagStats(name);

        if (stats.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(404, "해시태그를 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 특정 해시태그의 게시글 ID 목록 조회 API
     * GET /api/hashtags/posts?name=java
     */
    @GetMapping("/api/hashtags/posts")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPostsByHashtag(@RequestParam String name) {
        List<Long> boardIds = hashtagService.getBoardIdsByHashtag(name);
        Map<String, Object> data = Map.of(
                "hashtag", name,
                "boardIds", boardIds,
                "count", boardIds.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 특정 게시글의 해시태그 목록 조회 API
     * GET /api/hashtags/board/{boardId}
     */
    @GetMapping("/api/hashtags/board/{boardId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBoardHashtags(@PathVariable Long boardId) {
        List<Hashtag> hashtags = hashtagService.getBoardHashtags(boardId);
        List<Map<String, Object>> hashtagList = hashtags.stream()
                .<Map<String, Object>>map(h -> Map.of(
                        "id", h.getId(),
                        "name", h.getName(),
                        "useCount", h.getUseCount()
                ))
                .toList();
        Map<String, Object> data = Map.of(
                "boardId", boardId,
                "hashtags", hashtagList,
                "count", hashtags.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // ========== 해시태그 팔로우 기능 ==========

    /**
     * 해시태그 팔로우/언팔로우 API
     * POST /api/hashtags/follow?name=java
     */
    @PostMapping("/api/hashtags/follow")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleHashtagFollow(
            @RequestParam String name,
            @CurrentUser User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "로그인이 필요합니다."));
        }

        try {
            Map<String, Object> result = hashtagService.toggleHashtagFollow(currentUser.getId(), name);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 해시태그 상세 페이지
     * GET /hashtag/{name}
     */
    @GetMapping("/hashtag/{name}")
    public String hashtagPage(
            @PathVariable String name,
            @CurrentUser User currentUser,
            Model model) {
        Long userId = currentUser != null ? currentUser.getId() : null;

        // 해시태그 상세 정보 조회
        Map<String, Object> hashtagDetails = hashtagService.getHashtagDetails(name, userId);

        if (hashtagDetails.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 해시태그입니다.");
            return "error/404";
        }

        // 해시태그가 포함된 게시글 ID 목록 조회
        List<Long> boardIds = hashtagService.getBoardIdsByHashtag(name);

        // 게시글 정보 조회
        List<Board> boards = boardIds.stream()
                .map(id -> {
                    try {
                        return boardService.getBoardById(id);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(board -> board != null && !Boolean.TRUE.equals(board.getIsDraft()))
                .collect(Collectors.toList());

        model.addAttribute("hashtag", hashtagDetails);
        model.addAttribute("boards", boards);
        model.addAttribute("currentUser", currentUser);

        return "hashtag/view";
    }

    /**
     * 팔로우한 해시태그 피드 페이지
     * GET /hashtag/feed
     */
    @GetMapping("/hashtag/feed")
    public String hashtagFeed(@CurrentUser User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // 팔로우한 해시태그 목록
        List<Hashtag> followedHashtags = hashtagService.getFollowedHashtags(currentUser.getId());

        // 팔로우한 해시태그의 게시글 ID 목록
        List<Long> boardIds = hashtagService.getBoardIdsByFollowedHashtags(currentUser.getId());

        // 게시글 정보 조회
        List<Board> boards = boardIds.stream()
                .map(id -> {
                    try {
                        return boardService.getBoardById(id);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(board -> board != null && !Boolean.TRUE.equals(board.getIsDraft()))
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt())) // 최신순 정렬
                .collect(Collectors.toList());

        model.addAttribute("followedHashtags", followedHashtags);
        model.addAttribute("boards", boards);
        model.addAttribute("currentUser", currentUser);

        return "hashtag/feed";
    }

    /**
     * 사용자가 팔로우한 해시태그 목록 API
     * GET /api/hashtags/following
     */
    @GetMapping("/api/hashtags/following")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFollowedHashtags(
            @CurrentUser User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "로그인이 필요합니다."));
        }

        List<Hashtag> hashtags = hashtagService.getFollowedHashtags(currentUser.getId());
        List<Map<String, Object>> hashtagList = hashtags.stream()
                .<Map<String, Object>>map(h -> Map.of(
                        "id", h.getId(),
                        "name", h.getName(),
                        "useCount", h.getUseCount(),
                        "followerCount", hashtagService.getFollowerCount(h.getId())
                ))
                .toList();
        Map<String, Object> data = Map.of(
                "hashtags", hashtagList,
                "count", hashtags.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // ========== 해시태그 분석 기능 ==========

    /**
     * 시간대별 트렌딩 해시태그 조회 API
     * GET /api/hashtags/analytics/trending
     * @return 1시간, 1일, 7일, 30일 트렌딩 해시태그
     */
    @GetMapping("/api/hashtags/analytics/trending")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrendingByPeriod() {
        Map<String, List<Map<String, Object>>> trends = hashtagService.getTrendingByPeriod();
        Map<String, Object> data = Map.of(
                "trends", trends,
                "periods", List.of("1hour", "1day", "7days", "30days")
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 관련 해시태그 추천 API (co-occurrence 기반)
     * GET /api/hashtags/analytics/related?name=javascript&limit=10
     * @param name 기준 해시태그 이름
     * @param limit 조회할 관련 해시태그 수
     * @return 함께 사용된 해시태그 목록 (빈도순)
     */
    @GetMapping("/api/hashtags/analytics/related")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRelatedHashtags(
            @RequestParam String name,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> relatedHashtags = hashtagService.getRelatedHashtags(name, limit);
            Map<String, Object> data = Map.of(
                    "hashtag", name,
                    "relatedHashtags", relatedHashtags,
                    "count", relatedHashtags.size()
            );
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 워드클라우드 데이터 조회 API
     * GET /api/hashtags/analytics/wordcloud?limit=50
     * @param limit 조회할 해시태그 수
     * @return 해시태그별 빈도 데이터
     */
    @GetMapping("/api/hashtags/analytics/wordcloud")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWordCloudData(
            @RequestParam(defaultValue = "50") int limit) {
        List<Map<String, Object>> wordCloudData = hashtagService.getWordCloudData(limit);
        Map<String, Object> data = Map.of(
                "data", wordCloudData,
                "count", wordCloudData.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 전체 해시태그 통계 조회 API
     * GET /api/hashtags/analytics/overview
     * @return 전체 해시태그 통계 (총 개수, 총 사용 횟수, 인기 해시태그 등)
     */
    @GetMapping("/api/hashtags/analytics/overview")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverallStatistics() {
        Map<String, Object> statistics = hashtagService.getOverallStatistics();
        Map<String, Object> data = Map.of("statistics", statistics);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 해시태그별 게시글 수 통계 API (차트용)
     * GET /api/hashtags/analytics/post-counts?limit=20
     * @param limit 조회할 해시태그 수
     * @return 해시태그별 게시글 수 데이터
     */
    @GetMapping("/api/hashtags/analytics/post-counts")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPostCountStats(
            @RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> postCountStats = hashtagService.getHashtagPostCountStats(limit);
        Map<String, Object> data = Map.of(
                "data", postCountStats,
                "count", postCountStats.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 해시태그 분석 페이지
     * GET /hashtag/analytics
     */
    @GetMapping("/hashtag/analytics")
    public String analyticsPage(@CurrentUser User currentUser, Model model) {

        // 기본 통계 데이터 로드
        Map<String, Object> overallStats = hashtagService.getOverallStatistics();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("overallStats", overallStats);

        return "hashtag/analytics";
    }

    // ========== 해시태그 관리 기능 (관리자용) ==========

    /**
     * 금지 해시태그 설정/해제 API
     * POST /api/hashtags/admin/ban
     */
    @PostMapping("/api/hashtags/admin/ban")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> banHashtag(
            @RequestParam String name,
            @RequestParam boolean banned,
            @CurrentUser User currentUser) {
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error(403, "관리자 권한이 필요합니다."));
        }

        try {
            Map<String, Object> result = hashtagService.toggleBanHashtag(name, banned);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 해시태그 설명 수정 API
     * POST /api/hashtags/admin/description
     */
    @PostMapping("/api/hashtags/admin/description")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateDescription(
            @RequestParam String name,
            @RequestParam String description,
            @CurrentUser User currentUser) {
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error(403, "관리자 권한이 필요합니다."));
        }

        try {
            Map<String, Object> result = hashtagService.updateHashtagDescription(name, description);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 해시태그 병합 API
     * POST /api/hashtags/admin/merge
     */
    @PostMapping("/api/hashtags/admin/merge")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> mergeHashtags(
            @RequestParam String source,
            @RequestParam String target,
            @CurrentUser User currentUser) {
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error(403, "관리자 권한이 필요합니다."));
        }

        try {
            Map<String, Object> result = hashtagService.mergeHashtags(source, target);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 금지된 해시태그 목록 조회 API
     * GET /api/hashtags/admin/banned
     */
    @GetMapping("/api/hashtags/admin/banned")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBannedHashtags(
            @CurrentUser User currentUser) {
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error(403, "관리자 권한이 필요합니다."));
        }

        List<Map<String, Object>> banned = hashtagService.getBannedHashtags();
        Map<String, Object> data = Map.of(
                "hashtags", banned,
                "count", banned.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 병합된 해시태그 목록 조회 API
     * GET /api/hashtags/admin/merged
     */
    @GetMapping("/api/hashtags/admin/merged")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMergedHashtags(
            @CurrentUser User currentUser) {
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error(403, "관리자 권한이 필요합니다."));
        }

        List<Map<String, Object>> merged = hashtagService.getMergedHashtags();
        Map<String, Object> data = Map.of(
                "hashtags", merged,
                "count", merged.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 활성 해시태그 목록 조회 API (관리용)
     * GET /api/hashtags/admin/active
     */
    @GetMapping("/api/hashtags/admin/active")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getActiveHashtags(
            @CurrentUser User currentUser) {
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error(403, "관리자 권한이 필요합니다."));
        }

        List<Map<String, Object>> active = hashtagService.getActiveHashtagsForManagement();
        Map<String, Object> data = Map.of(
                "hashtags", active,
                "count", active.size()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 해시태그 관리 페이지 (관리자용)
     * GET /admin/hashtags
     */
    @GetMapping("/admin/hashtags")
    public String hashtagManagementPage(@CurrentUser User currentUser, Model model) {
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return "redirect:/board";
        }

        model.addAttribute("currentUser", currentUser);

        return "admin/hashtag-management";
    }

    /**
     * 해시태그 분석 페이지 (관리자용)
     * GET /admin/hashtag-analytics
     */
    @GetMapping("/admin/hashtag-analytics")
    public String hashtagAnalyticsPage(@CurrentUser User currentUser, Model model) {
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            return "redirect:/board";
        }

        model.addAttribute("currentUser", currentUser);

        return "admin/hashtag-analytics";
    }
}

package com.board.controller;

import com.board.entity.ExternalShare;
import com.board.entity.Share;
import com.board.entity.User;
import com.board.service.ShareService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;
    private final UserService userService;

    /**
     * 게시글 공유 토글 API
     * POST /api/shares/toggle/{boardId}
     */
    @PostMapping("/api/shares/toggle/{boardId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleShare(
            @PathVariable Long boardId,
            @RequestParam(required = false) String quoteContent) {

        try {
            Map<String, Object> result = shareService.toggleShare(boardId, quoteContent);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 게시글 공유 정보 조회 API
     * GET /api/shares/info/{boardId}
     */
    @GetMapping("/api/shares/info/{boardId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getShareInfo(@PathVariable Long boardId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        Long userId = currentUser != null ? currentUser.getId() : null;

        Map<String, Object> info = shareService.getShareInfo(boardId, userId);
        return ResponseEntity.ok(info);
    }

    /**
     * 게시글을 공유한 사용자 목록 조회 API
     * GET /api/shares/users/{boardId}
     */
    @GetMapping("/api/shares/users/{boardId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getShareUsers(@PathVariable Long boardId) {
        List<Map<String, Object>> users = shareService.getShareUsers(boardId);

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("count", users.size());
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자가 공유한 게시글 목록 조회 API
     * GET /api/shares/my-shares
     */
    @GetMapping("/api/shares/my-shares")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMyShares() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        List<Share> shares = shareService.getSharedBoardsByUserId(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("shares", shares);
        response.put("count", shares.size());
        return ResponseEntity.ok(response);
    }

    // ========== 외부 공유 통계 API ==========

    /**
     * 외부 공유 추적 API
     * POST /api/shares/track-external
     */
    @PostMapping("/api/shares/track-external")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> trackExternalShare(
            @RequestParam Long boardId,
            @RequestParam String platform,
            HttpServletRequest request) {

        try {
            // 플랫폼 검증
            ExternalShare.SharePlatform sharePlatform;
            try {
                sharePlatform = ExternalShare.SharePlatform.valueOf(platform.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "잘못된 플랫폼입니다."));
            }

            // 현재 사용자 정보
            User currentUser = AuthenticationUtils.getCurrentUser(userService);
            Long userId = currentUser != null ? currentUser.getId() : null;

            // IP 주소 및 User Agent 추출
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            // 외부 공유 추적
            shareService.trackExternalShare(boardId, sharePlatform, userId, ipAddress, userAgent);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "외부 공유가 추적되었습니다.");
            response.put("platform", platform);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 게시글 공유 통계 조회 API
     * GET /api/shares/statistics/{boardId}
     */
    @GetMapping("/api/shares/statistics/{boardId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getShareStatistics(@PathVariable Long boardId) {
        try {
            Map<String, Object> statistics = shareService.getShareStatistics(boardId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 가장 많이 공유된 게시글 순위 API (전체 외부 공유)
     * GET /api/shares/ranking?limit=10
     */
    @GetMapping("/api/shares/ranking")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMostSharedBoards(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> ranking = shareService.getMostSharedBoards(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("ranking", ranking);
            response.put("count", ranking.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 플랫폼별 가장 많이 공유된 게시글 순위 API
     * GET /api/shares/ranking/platform/{platform}?limit=10
     */
    @GetMapping("/api/shares/ranking/platform/{platform}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMostSharedBoardsByPlatform(
            @PathVariable String platform,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // 플랫폼 검증
            ExternalShare.SharePlatform sharePlatform;
            try {
                sharePlatform = ExternalShare.SharePlatform.valueOf(platform.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "잘못된 플랫폼입니다. (TWITTER, FACEBOOK, LINKEDIN, KAKAO, LINK_COPY, QR_CODE)"));
            }

            List<Map<String, Object>> ranking = shareService.getMostSharedBoardsByPlatform(sharePlatform, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("platform", platform);
            response.put("ranking", ranking);
            response.put("count", ranking.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 최근 외부 공유 목록 조회 API
     * GET /api/shares/recent-external?limit=10
     */
    @GetMapping("/api/shares/recent-external")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRecentExternalShares(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> recentShares = shareService.getRecentExternalShares(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("shares", recentShares);
            response.put("count", recentShares.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 특정 기간 동안의 공유 통계 API
     * GET /api/shares/statistics/period?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59
     */
    @GetMapping("/api/shares/statistics/period")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getShareStatsByPeriod(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);

            Map<String, Object> statistics = shareService.getShareStatsByPeriod(start, end);
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "날짜 형식이 올바르지 않습니다. (예: 2025-01-01T00:00:00)"));
        }
    }

    /**
     * 클라이언트 IP 주소 추출 헬퍼 메서드
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}

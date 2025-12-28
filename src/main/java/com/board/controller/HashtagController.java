package com.board.controller;

import com.board.entity.Hashtag;
import com.board.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    /**
     * 해시태그 자동완성 API
     * GET /api/hashtags/search?q=java
     */
    @GetMapping("/api/hashtags/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchHashtags(@RequestParam String q) {
        List<Map<String, Object>> hashtags = hashtagService.searchHashtags(q);

        Map<String, Object> response = new HashMap<>();
        response.put("hashtags", hashtags);
        response.put("count", hashtags.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 인기 해시태그 조회 API
     * GET /api/hashtags/popular?limit=20
     */
    @GetMapping("/api/hashtags/popular")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPopularHashtags(
            @RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> hashtags = hashtagService.getPopularHashtags(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("hashtags", hashtags);
        response.put("count", hashtags.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 트렌딩 해시태그 조회 API (최근 24시간)
     * GET /api/hashtags/trending?limit=10
     */
    @GetMapping("/api/hashtags/trending")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTrendingHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> hashtags = hashtagService.getTrendingHashtags(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("hashtags", hashtags);
        response.put("count", hashtags.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 최근 사용된 해시태그 조회 API
     * GET /api/hashtags/recent?limit=10
     */
    @GetMapping("/api/hashtags/recent")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRecentHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> hashtags = hashtagService.getRecentHashtags(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("hashtags", hashtags);
        response.put("count", hashtags.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 해시태그 통계 조회 API
     * GET /api/hashtags/stats?name=java
     */
    @GetMapping("/api/hashtags/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getHashtagStats(@RequestParam String name) {
        Map<String, Object> stats = hashtagService.getHashtagStats(name);

        if (stats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * 특정 해시태그의 게시글 ID 목록 조회 API
     * GET /api/hashtags/posts?name=java
     */
    @GetMapping("/api/hashtags/posts")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPostsByHashtag(@RequestParam String name) {
        List<Long> boardIds = hashtagService.getBoardIdsByHashtag(name);

        Map<String, Object> response = new HashMap<>();
        response.put("hashtag", name);
        response.put("boardIds", boardIds);
        response.put("count", boardIds.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글의 해시태그 목록 조회 API
     * GET /api/hashtags/board/{boardId}
     */
    @GetMapping("/api/hashtags/board/{boardId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBoardHashtags(@PathVariable Long boardId) {
        List<Hashtag> hashtags = hashtagService.getBoardHashtags(boardId);

        Map<String, Object> response = new HashMap<>();
        response.put("boardId", boardId);
        response.put("hashtags", hashtags.stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", h.getId());
                    map.put("name", h.getName());
                    map.put("useCount", h.getUseCount());
                    return map;
                })
                .toList());
        response.put("count", hashtags.size());

        return ResponseEntity.ok(response);
    }
}

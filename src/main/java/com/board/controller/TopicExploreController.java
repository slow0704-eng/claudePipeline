package com.board.controller;

import com.board.entity.User;
import com.board.service.TopicExploreService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TopicExploreController {

    private final TopicExploreService topicExploreService;
    private final UserService userService;

    /**
     * 토픽 탐색 페이지
     * GET /topics/explore
     */
    @GetMapping("/topics/explore")
    public String explorePage(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        Long userId = currentUser != null ? currentUser.getId() : null;

        // 탐색 데이터 조회
        Map<String, Object> exploreData = topicExploreService.getExploreData(userId);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("exploreData", exploreData);

        return "topic/explore";
    }

    /**
     * 인기 토픽 조회 API
     * GET /api/topics/explore/popular?limit=10
     */
    @GetMapping("/api/topics/explore/popular")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPopularTopics(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Map<String, Object>> topics = topicExploreService.getPopularTopics(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("topics", topics);
            response.put("count", topics.size());
            response.put("category", "popular");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 새로운 토픽 조회 API
     * GET /api/topics/explore/new?limit=10
     */
    @GetMapping("/api/topics/explore/new")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNewTopics(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Map<String, Object>> topics = topicExploreService.getNewTopics(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("topics", topics);
            response.put("count", topics.size());
            response.put("category", "new");
            response.put("description", "최근 30일 이내에 생성된 토픽");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 트렌딩 토픽 조회 API
     * GET /api/topics/explore/trending?limit=10
     */
    @GetMapping("/api/topics/explore/trending")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTrendingTopics(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Map<String, Object>> topics = topicExploreService.getTrendingTopics(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("topics", topics);
            response.put("count", topics.size());
            response.put("category", "trending");
            response.put("description", "최근 7일간 성장세가 높은 토픽");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 팔로워 수 기준 인기 토픽 조회 API
     * GET /api/topics/explore/most-followed?limit=10
     */
    @GetMapping("/api/topics/explore/most-followed")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMostFollowedTopics(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Map<String, Object>> topics = topicExploreService.getTopicsByFollowerCount(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("topics", topics);
            response.put("count", topics.size());
            response.put("category", "most-followed");
            response.put("description", "팔로워가 가장 많은 토픽");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 최근 활발한 토픽 조회 API
     * GET /api/topics/explore/active?limit=10
     */
    @GetMapping("/api/topics/explore/active")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getActiveTopics(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Map<String, Object>> topics = topicExploreService.getActiveTopics(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("topics", topics);
            response.put("count", topics.size());
            response.put("category", "active");
            response.put("description", "최근 7일간 활발한 토픽");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 카테고리별 토픽 조회 API (통합)
     * GET /api/topics/explore?category=popular&limit=10
     */
    @GetMapping("/api/topics/explore")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopicsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Map<String, Object>> topics = topicExploreService.getTopicsByCategory(category, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("topics", topics);
            response.put("count", topics.size());
            response.put("category", category);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "토픽 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 토픽 검색 API
     * GET /api/topics/explore/search?q=spring&limit=10
     */
    @GetMapping("/api/topics/explore/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchTopics(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Map<String, Object>> topics = topicExploreService.searchTopicsForExplore(q, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("topics", topics);
            response.put("count", topics.size());
            response.put("query", q);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 토픽 상세 정보 조회 API
     * GET /api/topics/explore/{topicId}/detail
     */
    @GetMapping("/api/topics/explore/{topicId}/detail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopicDetail(@PathVariable Long topicId) {
        try {
            Map<String, Object> detail = topicExploreService.getTopicDetailForExplore(topicId);

            if (detail.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(detail);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 통합 탐색 데이터 API
     * GET /api/topics/explore/all
     */
    @GetMapping("/api/topics/explore/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllExploreData() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        Long userId = currentUser != null ? currentUser.getId() : null;

        try {
            Map<String, Object> exploreData = topicExploreService.getExploreData(userId);
            return ResponseEntity.ok(exploreData);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

package com.board.controller;

import com.board.entity.User;
import com.board.service.TopicRecommendationService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TopicRecommendationController {

    private final TopicRecommendationService recommendationService;
    private final UserService userService;

    /**
     * 하이브리드 추천 API (협업 필터링 + 컨텐츠 기반)
     * GET /api/topics/recommended?limit=10
     */
    @GetMapping("/api/topics/recommended")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRecommendedTopics(
            @RequestParam(defaultValue = "10") int limit) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            List<Map<String, Object>> recommendations = recommendationService.getRecommendedTopics(
                currentUser.getId(),
                limit
            );

            Map<String, Object> response = new HashMap<>();
            response.put("topics", recommendations);
            response.put("count", recommendations.size());
            response.put("method", "hybrid");
            response.put("description", "협업 필터링과 컨텐츠 기반 추천을 결합한 개인화 추천");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 협업 필터링 추천만 조회 API
     * GET /api/topics/recommended/collaborative?limit=10
     */
    @GetMapping("/api/topics/recommended/collaborative")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCollaborativeRecommendations(
            @RequestParam(defaultValue = "10") int limit) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            List<Map<String, Object>> recommendations = recommendationService.getCollaborativeOnlyRecommendations(
                currentUser.getId(),
                limit
            );

            Map<String, Object> response = new HashMap<>();
            response.put("topics", recommendations);
            response.put("count", recommendations.size());
            response.put("method", "collaborative");
            response.put("description", "비슷한 사용자들이 팔로우한 토픽 추천");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 컨텐츠 기반 추천만 조회 API
     * GET /api/topics/recommended/content-based?limit=10
     */
    @GetMapping("/api/topics/recommended/content-based")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getContentBasedRecommendations(
            @RequestParam(defaultValue = "10") int limit) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            List<Map<String, Object>> recommendations = recommendationService.getContentOnlyRecommendations(
                currentUser.getId(),
                limit
            );

            Map<String, Object> response = new HashMap<>();
            response.put("topics", recommendations);
            response.put("count", recommendations.size());
            response.put("method", "content-based");
            response.put("description", "활동 기반 관련 토픽 추천");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 추천 방식 선택 API (통합)
     * GET /api/topics/recommended/by-method?method=hybrid&limit=10
     */
    @GetMapping("/api/topics/recommended/by-method")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRecommendationsByMethod(
            @RequestParam(defaultValue = "hybrid") String method,
            @RequestParam(defaultValue = "10") int limit) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            List<Map<String, Object>> recommendations;
            String description;

            switch (method.toLowerCase()) {
                case "collaborative":
                    recommendations = recommendationService.getCollaborativeOnlyRecommendations(
                        currentUser.getId(), limit
                    );
                    description = "비슷한 사용자들이 팔로우한 토픽 추천";
                    break;

                case "content":
                case "content-based":
                    recommendations = recommendationService.getContentOnlyRecommendations(
                        currentUser.getId(), limit
                    );
                    description = "활동 기반 관련 토픽 추천";
                    break;

                case "hybrid":
                default:
                    recommendations = recommendationService.getRecommendedTopics(
                        currentUser.getId(), limit
                    );
                    description = "협업 필터링과 컨텐츠 기반 추천을 결합한 개인화 추천";
                    break;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("topics", recommendations);
            response.put("count", recommendations.size());
            response.put("method", method);
            response.put("description", description);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

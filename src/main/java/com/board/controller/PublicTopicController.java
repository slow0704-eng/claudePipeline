package com.board.controller;

import com.board.service.TopicService;
import com.board.service.BoardTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class PublicTopicController {

    private final TopicService topicService;
    private final BoardTopicService boardTopicService;

    /**
     * 토픽 검색 (자동완성)
     * GET /api/topics/search?q=react
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTopics(@RequestParam String q) {
        try {
            List<Map<String, Object>> results = topicService.searchTopics(q);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "topics", results
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 인기 토픽 조회
     * GET /api/topics/popular?limit=10
     */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularTopics(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> topics = topicService.getPopularTopics(limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "topics", topics
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 관련 토픽 추천
     * GET /api/topics/{topicId}/related?limit=5
     */
    @GetMapping("/{topicId}/related")
    public ResponseEntity<Map<String, Object>> getRelatedTopics(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<Map<String, Object>> related = topicService.getRelatedTopics(topicId, limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "topics", related
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 게시글의 토픽 경로 조회
     * GET /api/topics/board/{boardId}/paths
     */
    @GetMapping("/board/{boardId}/paths")
    public ResponseEntity<Map<String, Object>> getBoardTopicPaths(@PathVariable Long boardId) {
        try {
            List<String> paths = boardTopicService.getBoardTopicPaths(boardId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "paths", paths
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}

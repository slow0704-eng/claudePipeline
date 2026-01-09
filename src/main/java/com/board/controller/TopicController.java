package com.board.controller;

import com.board.entity.Topic;
import com.board.entity.TopicMergeHistory;
import com.board.entity.User;
import com.board.service.TopicService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final UserService userService;

    // ========== Page Rendering ==========

    @GetMapping("/topic-management")
    public String topicManagement(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        model.addAttribute("currentUser", currentUser);
        return "admin/topic-management";
    }

    // ========== API Endpoints ==========

    @GetMapping("/api/topics/tree")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopicTree() {
        try {
            List<Map<String, Object>> tree = topicService.getTopicTree();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "topicTree", tree
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/api/topics/active")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getActiveTopics() {
        try {
            List<Topic> topics = topicService.getActiveTopics();
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

    @GetMapping("/api/topics/merged")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMergedTopics() {
        try {
            List<Topic> topics = topicService.getMergedTopics();
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

    @GetMapping("/api/topics/merge-history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMergeHistory() {
        try {
            List<TopicMergeHistory> history = topicService.getAllMergeHistory();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "history", history
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/api/topics/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = topicService.getOverallStatistics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "statistics", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/api/topics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createTopic(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long parentId = request.get("parentId") != null && !request.get("parentId").toString().isEmpty() ?
                    Long.parseLong(request.get("parentId").toString()) : null;
            String icon = (String) request.get("icon");
            String color = (String) request.get("color");
            Integer displayOrder = request.get("displayOrder") != null ?
                    Integer.parseInt(request.get("displayOrder").toString()) : null;

            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("토픽명은 필수입니다.");
            }

            Topic topic = topicService.createTopic(name, description, parentId,
                                                  icon, color, displayOrder);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "토픽이 생성되었습니다.",
                "topic", topic
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/api/topics/{topicId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTopic(
            @PathVariable Long topicId,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long parentId = request.get("parentId") != null && !request.get("parentId").toString().isEmpty() ?
                    Long.parseLong(request.get("parentId").toString()) : null;
            String icon = (String) request.get("icon");
            String color = (String) request.get("color");
            Integer displayOrder = request.get("displayOrder") != null ?
                    Integer.parseInt(request.get("displayOrder").toString()) : null;
            Boolean enabled = request.get("enabled") != null ?
                    Boolean.parseBoolean(request.get("enabled").toString()) : null;

            Topic topic = topicService.updateTopic(topicId, name, description, parentId,
                                                  icon, color, displayOrder, enabled);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "토픽이 수정되었습니다.",
                "topic", topic
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/api/topics/{topicId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteTopic(@PathVariable Long topicId) {
        try {
            topicService.deleteTopic(topicId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "토픽이 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/api/topics/{topicId}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleTopicStatus(@PathVariable Long topicId) {
        try {
            Topic topic = topicService.toggleTopicStatus(topicId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", topic.getEnabled() ? "토픽이 활성화되었습니다." : "토픽이 비활성화되었습니다.",
                "enabled", topic.getEnabled()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/api/topics/merge")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> mergeTopics(@RequestBody Map<String, Object> request) {
        try {
            Long sourceId = Long.parseLong(request.get("sourceId").toString());
            Long targetId = Long.parseLong(request.get("targetId").toString());
            String notes = (String) request.get("notes");

            User currentUser = AuthenticationUtils.getCurrentUser(userService);
            Map<String, Object> result = topicService.mergeTopics(sourceId, targetId,
                                                                  currentUser.getId(), notes);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}

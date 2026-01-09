package com.board.controller;

import com.board.entity.Topic;
import com.board.entity.User;
import com.board.service.TopicFollowService;
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
public class TopicFollowController {

    private final TopicFollowService topicFollowService;
    private final UserService userService;

    /**
     * 토픽 팔로우/언팔로우 토글 API
     * POST /api/topics/{topicId}/follow
     */
    @PostMapping("/api/topics/{topicId}/follow")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleTopicFollow(@PathVariable Long topicId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            Map<String, Object> result = topicFollowService.toggleTopicFollow(currentUser.getId(), topicId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 팔로우 여부 확인 API
     * GET /api/topics/{topicId}/follow-status
     */
    @GetMapping("/api/topics/{topicId}/follow-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFollowStatus(@PathVariable Long topicId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        Map<String, Object> response = new HashMap<>();
        if (currentUser == null) {
            response.put("isFollowing", false);
            response.put("followerCount", topicFollowService.getFollowerCount(topicId));
        } else {
            boolean isFollowing = topicFollowService.isFollowingTopic(currentUser.getId(), topicId);
            long followerCount = topicFollowService.getFollowerCount(topicId);

            response.put("isFollowing", isFollowing);
            response.put("followerCount", followerCount);
            response.put("topicId", topicId);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 팔로우한 토픽 목록 조회 API
     * GET /api/topics/following
     */
    @GetMapping("/api/topics/following")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFollowedTopics() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        List<Map<String, Object>> topics = topicFollowService.getFollowedTopicsWithDetails(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("topics", topics);
        response.put("count", topics.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 팔로우한 토픽 목록 조회 API (간단 버전)
     * GET /api/topics/following/simple
     */
    @GetMapping("/api/topics/following/simple")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFollowedTopicsSimple() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        List<Topic> topics = topicFollowService.getFollowedTopics(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("topics", topics.stream()
                .map(t -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", t.getId());
                    map.put("name", t.getName());
                    map.put("icon", t.getIcon());
                    map.put("color", t.getColor());
                    return map;
                })
                .toList());
        response.put("count", topics.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자의 토픽 팔로우 수 조회 API
     * GET /api/topics/following/count
     */
    @GetMapping("/api/topics/following/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFollowCount() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        long count = topicFollowService.getUserFollowCount(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", currentUser.getId());
        response.put("followCount", count);

        return ResponseEntity.ok(response);
    }

    /**
     * 토픽의 팔로워 ID 목록 조회 API
     * GET /api/topics/{topicId}/followers
     */
    @GetMapping("/api/topics/{topicId}/followers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFollowers(@PathVariable Long topicId) {
        List<Long> followerIds = topicFollowService.getFollowerIds(topicId);

        Map<String, Object> response = new HashMap<>();
        response.put("topicId", topicId);
        response.put("followerIds", followerIds);
        response.put("followerCount", followerIds.size());

        return ResponseEntity.ok(response);
    }
}

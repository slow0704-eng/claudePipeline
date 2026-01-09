package com.board.controller;

import com.board.dto.TopicFeedItemDTO;
import com.board.entity.Topic;
import com.board.entity.User;
import com.board.service.TopicFeedService;
import com.board.service.TopicFollowService;
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
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class TopicFeedController {

    private final TopicFeedService topicFeedService;
    private final TopicFollowService topicFollowService;
    private final UserService userService;

    /**
     * 토픽 피드 API
     * GET /api/topics/feed?topicIds=1,2,3&page=0&size=20
     */
    @GetMapping("/api/topics/feed")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopicFeed(
            @RequestParam(required = false) List<Long> topicIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            List<TopicFeedItemDTO> feedItems = topicFeedService.getTopicFeed(
                currentUser.getId(),
                topicIds,
                page,
                size
            );

            long totalEstimate = topicFeedService.estimateFeedItemCount(
                currentUser.getId(),
                topicIds
            );

            Map<String, Object> response = new HashMap<>();
            response.put("items", feedItems);
            response.put("count", feedItems.size());
            response.put("page", page);
            response.put("size", size);
            response.put("totalEstimate", totalEstimate);
            response.put("hasMore", feedItems.size() == size);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 단일 토픽 피드 API
     * GET /api/topics/{topicId}/feed?page=0&size=20
     */
    @GetMapping("/api/topics/{topicId}/feed")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopicFeedByTopicId(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            List<TopicFeedItemDTO> feedItems = topicFeedService.getTopicFeedByTopicId(
                currentUser.getId(),
                topicId,
                page,
                size
            );

            Map<String, Object> response = new HashMap<>();
            response.put("items", feedItems);
            response.put("count", feedItems.size());
            response.put("page", page);
            response.put("size", size);
            response.put("topicId", topicId);
            response.put("hasMore", feedItems.size() == size);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 토픽 피드 페이지
     * GET /topics/feed
     */
    @GetMapping("/topics/feed")
    public String topicFeedPage(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // 팔로우한 토픽 목록
        List<Topic> followedTopics = topicFollowService.getFollowedTopics(currentUser.getId());

        // 초기 피드 데이터 (첫 페이지)
        List<TopicFeedItemDTO> initialFeed = topicFeedService.getAllFollowedTopicsFeed(
            currentUser.getId(),
            0,
            20
        );

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("followedTopics", followedTopics);
        model.addAttribute("initialFeed", initialFeed);
        model.addAttribute("followedTopicsCount", followedTopics.size());

        return "topic/feed";
    }

    /**
     * 단일 토픽 피드 페이지
     * GET /topics/{topicId}/feed
     */
    @GetMapping("/topics/{topicId}/feed")
    public String singleTopicFeedPage(@PathVariable Long topicId, Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // 초기 피드 데이터 (첫 페이지)
        List<TopicFeedItemDTO> initialFeed = topicFeedService.getTopicFeedByTopicId(
            currentUser.getId(),
            topicId,
            0,
            20
        );

        // 팔로우한 토픽 목록 (사이드바용)
        List<Topic> followedTopics = topicFollowService.getFollowedTopics(currentUser.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("topicId", topicId);
        model.addAttribute("followedTopics", followedTopics);
        model.addAttribute("initialFeed", initialFeed);

        return "topic/feed";
    }
}

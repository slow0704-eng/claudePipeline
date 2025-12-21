package com.board.controller;

import com.board.entity.User;
import com.board.service.FollowService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/api/follow/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleFollow(@PathVariable Long userId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        try {
            Map<String, Object> result = followService.toggleFollow(currentUser.getId(), userId);
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/api/follow/status/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFollowStatus(@PathVariable Long userId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        boolean isFollowing = false;
        if (currentUser != null) {
            isFollowing = followService.isFollowing(currentUser.getId(), userId);
        }

        long followerCount = followService.getFollowerCount(userId);
        long followingCount = followService.getFollowingCount(userId);

        return ResponseEntity.ok(Map.of(
            "isFollowing", isFollowing,
            "followerCount", followerCount,
            "followingCount", followingCount
        ));
    }
}

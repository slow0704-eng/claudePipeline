package com.board.controller;

import com.board.entity.Share;
import com.board.entity.User;
import com.board.service.ShareService;
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
}

package com.board.controller;

import com.board.enums.TargetType;
import com.board.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{id}")
    public ResponseEntity<?> togglePostLike(@PathVariable Long id) {
        try {
            Map<String, Object> result = likeService.toggleLike(TargetType.POST, id);
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<?> toggleCommentLike(@PathVariable Long id) {
        try {
            Map<String, Object> result = likeService.toggleLike(TargetType.COMMENT, id);
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

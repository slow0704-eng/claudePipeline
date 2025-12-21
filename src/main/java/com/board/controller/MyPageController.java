package com.board.controller;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.service.BoardService;
import com.board.service.BookmarkService;
import com.board.service.FollowService;
import com.board.service.MessageService;
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
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final BoardService boardService;
    private final UserService userService;
    private final FollowService followService;
    private final BookmarkService bookmarkService;
    private final MessageService messageService;

    @GetMapping
    public String myPage(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Get user's posts
        List<Board> myPosts = boardService.getBoardsByUserId(currentUser.getId());

        // Get draft count
        long draftCount = boardService.getDraftCountByUserId(currentUser.getId());

        // Get bookmark count
        long bookmarkCount = bookmarkService.getUserBookmarkCount(currentUser.getId());

        // Get follow stats
        long followerCount = followService.getFollowerCount(currentUser.getId());
        long followingCount = followService.getFollowingCount(currentUser.getId());

        // Get unread message count
        long unreadMessageCount = messageService.getUnreadMessageCount(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("postCount", myPosts.size());
        model.addAttribute("draftCount", draftCount);
        model.addAttribute("bookmarkCount", bookmarkCount);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("unreadMessageCount", unreadMessageCount);

        return "mypage/mypage";
    }

    @GetMapping("/posts")
    public String myPosts(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        List<Board> myPosts = boardService.getBoardsByUserId(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("myPosts", myPosts);

        return "mypage/my-posts";
    }

    @PostMapping("/update-nickname")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateNickname(@RequestBody Map<String, String> request) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        try {
            String newNickname = request.get("nickname");
            if (newNickname == null || newNickname.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "닉네임을 입력해주세요."));
            }

            userService.updateNickname(currentUser.getId(), newNickname.trim());
            return ResponseEntity.ok(Map.of("success", true, "message", "닉네임이 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/update-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, String> request) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");

            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "현재 비밀번호를 입력해주세요."));
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "새 비밀번호를 입력해주세요."));
            }

            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "새 비밀번호가 일치하지 않습니다."));
            }

            if (newPassword.length() < 4) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "비밀번호는 최소 4자 이상이어야 합니다."));
            }

            userService.updatePassword(currentUser.getId(), currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호가 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}

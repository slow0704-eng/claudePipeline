package com.board.controller;

import com.board.dto.TimelineItemDTO;
import com.board.util.ApiResponse;
import com.board.entity.Board;
import com.board.entity.User;
import com.board.service.BoardService;
import com.board.service.BookmarkService;
import com.board.service.FollowService;
import com.board.service.MessageService;
import com.board.service.TimelineService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import com.board.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    private final TimelineService timelineService;

    @GetMapping
    public String myPage(@CurrentUser User currentUser, Model model) {
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
    public String myPosts(@CurrentUser User currentUser, Model model) {
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
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            @RequestBody Map<String, String> request,
            @CurrentUser User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "로그인이 필요합니다."));
        }

        try {
            String newNickname = request.get("nickname");
            if (newNickname == null || newNickname.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "닉네임을 입력해주세요."));
            }

            userService.updateNickname(currentUser.getId(), newNickname.trim());
            return ResponseEntity.ok(ApiResponse.success("닉네임이 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/update-password")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody Map<String, String> request,
            @CurrentUser User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "로그인이 필요합니다."));
        }

        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");

            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "현재 비밀번호를 입력해주세요."));
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "새 비밀번호를 입력해주세요."));
            }

            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "새 비밀번호가 일치하지 않습니다."));
            }

            if (newPassword.length() < 4) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "비밀번호는 최소 4자 이상이어야 합니다."));
            }

            userService.updatePassword(currentUser.getId(), currentPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 회원 탈퇴 페이지
     */
    @GetMapping("/delete-account")
    public String deleteAccountPage(@CurrentUser User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", currentUser);
        return "mypage/delete-account";
    }

    /**
     * 회원 탈퇴 처리
     */
    @PostMapping("/delete-account")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @RequestBody Map<String, String> request,
            @CurrentUser User currentUser,
            HttpServletRequest httpRequest) {
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "로그인이 필요합니다."));
        }

        try {
            String password = request.get("password");
            String reason = request.get("reason");

            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "비밀번호를 입력해주세요."));
            }

            // 회원 탈퇴 처리
            userService.deleteUser(currentUser.getId(), password, reason);

            // 세션 무효화 및 로그아웃
            SecurityContextHolder.clearContext();
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            return ResponseEntity.ok(ApiResponse.success(
                    "회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 타임라인 피드 조회 API (무한 스크롤)
     */
    @GetMapping("/timeline")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTimelineFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "로그인이 필요합니다."));
        }

        List<TimelineItemDTO> items = timelineService.getTimelineFeed(currentUser.getId(), page, size);
        boolean hasMore = items.size() == size; // 다음 페이지 존재 여부

        Map<String, Object> data = Map.of(
                "items", items,
                "hasMore", hasMore,
                "currentPage", page
        );

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}

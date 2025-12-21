package com.board.controller;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.service.BookmarkService;
import com.board.service.UserService;
import com.board.util.ApiResponse;
import com.board.util.AuthenticationUtils;
import com.board.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserService userService;

    /**
     * 북마크 토글 API (추가/제거)
     */
    @PostMapping("/toggle/{boardId}")
    @ResponseBody
    public ResponseEntity<ApiResponse> toggleBookmark(@PathVariable Long boardId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseBuilder.unauthorized("로그인이 필요합니다.");
        }

        try {
            Map<String, Object> result = bookmarkService.toggleBookmark(currentUser.getId(), boardId);
            return ResponseBuilder.success(result);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }

    /**
     * 북마크 목록 페이지
     */
    @GetMapping
    public String bookmarkList(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        List<Board> bookmarkedBoards = bookmarkService.getBookmarkedBoards(currentUser.getId());
        long bookmarkCount = bookmarkService.getUserBookmarkCount(currentUser.getId());

        model.addAttribute("bookmarkedBoards", bookmarkedBoards);
        model.addAttribute("bookmarkCount", bookmarkCount);

        return "board/bookmarks";
    }

    /**
     * 북마크 여부 확인 API
     */
    @GetMapping("/check/{boardId}")
    @ResponseBody
    public ResponseEntity<ApiResponse> checkBookmark(@PathVariable Long boardId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseBuilder.success(Map.of("bookmarked", false));
        }

        boolean isBookmarked = bookmarkService.isBookmarked(currentUser.getId(), boardId);
        return ResponseBuilder.success(Map.of("bookmarked", isBookmarked));
    }
}

package com.board.controller;

import com.board.entity.Board;
import com.board.entity.Report;
import com.board.entity.User;
import com.board.enums.ReportStatus;
import com.board.repository.BoardRepository;
import com.board.repository.CommentRepository;
import com.board.repository.LikeRepository;
import com.board.repository.ReportRepository;
import com.board.repository.UserRepository;
import com.board.service.BoardService;
import com.board.service.ReportService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BoardService boardService;
    private final ReportService reportService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ReportRepository reportRepository;

    @GetMapping
    public String dashboard(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // Statistics
        long totalUsers = userRepository.count();
        long totalBoards = boardRepository.count();
        long totalComments = commentRepository.count();
        long totalLikes = likeRepository.count();
        long pendingReports = reportService.getPendingReportCount();

        // Recent boards
        List<Board> recentBoards = boardRepository.findTop10ByOrderByCreatedAtDesc();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalBoards", totalBoards);
        model.addAttribute("totalComments", totalComments);
        model.addAttribute("totalLikes", totalLikes);
        model.addAttribute("pendingReports", pendingReports);
        model.addAttribute("recentBoards", recentBoards);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<User> users = userRepository.findAll();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", users);

        return "admin/users";
    }

    @GetMapping("/boards")
    public String boards(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<Board> boards = boardRepository.findAll();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("boards", boards);

        return "admin/boards";
    }

    @PostMapping("/users/{userId}/toggle-enabled")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleUserEnabled(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            user.setEnabled(!user.isEnabled());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "enabled", user.isEnabled(),
                "message", user.isEnabled() ? "사용자가 활성화되었습니다." : "사용자가 비활성화되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/boards/{boardId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteBoard(@PathVariable Long boardId) {
        try {
            boardRepository.deleteById(boardId);
            return ResponseEntity.ok(Map.of("success", true, "message", "게시글이 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/change-role")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changeUserRole(@PathVariable Long userId,
                                                               @RequestBody Map<String, String> request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            String newRole = request.get("role");
            if (newRole == null || (!newRole.equals("MEMBER") && !newRole.equals("ADMIN"))) {
                throw new RuntimeException("올바르지 않은 권한입니다.");
            }

            user.setRole(com.board.enums.UserRole.valueOf(newRole));
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "권한이 변경되었습니다.",
                "role", newRole
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 신고 관리 페이지
     */
    @GetMapping("/reports")
    public String reports(@RequestParam(value = "status", required = false) String status,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        org.springframework.data.domain.Page<Report> reportPage;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        if (status != null && !status.isEmpty()) {
            ReportStatus reportStatus = ReportStatus.valueOf(status);
            reportPage = reportService.getReportsByStatus(reportStatus, pageable);
        } else {
            reportPage = reportService.getAllReports(pageable);
        }

        // 통계
        long pendingCount = reportService.getPendingReportCount();
        long totalCount = reportRepository.count();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("reportPage", reportPage);
        model.addAttribute("currentStatus", status);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalCount", totalCount);

        return "admin/reports";
    }

    /**
     * 신고 승인 API
     */
    @PostMapping("/reports/{reportId}/approve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approveReport(
            @PathVariable Long reportId,
            @RequestParam(value = "comment", required = false) String comment) {
        try {
            User currentUser = AuthenticationUtils.getCurrentUser(userService);
            reportService.approveReport(reportId, currentUser.getId(), comment);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "신고가 승인되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 신고 반려 API
     */
    @PostMapping("/reports/{reportId}/reject")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> rejectReport(
            @PathVariable Long reportId,
            @RequestParam(value = "comment", required = false) String comment) {
        try {
            User currentUser = AuthenticationUtils.getCurrentUser(userService);
            reportService.rejectReport(reportId, currentUser.getId(), comment);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "신고가 반려되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}

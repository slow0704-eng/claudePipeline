package com.board.controller;

import com.board.entity.Attachment;
import com.board.entity.BannedWord;
import com.board.entity.Board;
import com.board.entity.Report;
import com.board.entity.User;
import com.board.enums.BannedWordAction;
import com.board.enums.BoardStatus;
import com.board.enums.ReportStatus;
import com.board.repository.BoardRepository;
import com.board.repository.CommentRepository;
import com.board.repository.LikeRepository;
import com.board.repository.ReportRepository;
import com.board.repository.UserRepository;
import com.board.service.BannedWordService;
import com.board.service.BoardService;
import com.board.service.FileStorageService;
import com.board.service.ReportService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final com.board.service.CategoryService categoryService;
    private final BannedWordService bannedWordService;
    private final FileStorageService fileStorageService;
    private final com.board.service.RoleManagementService roleManagementService;
    private final com.board.service.MenuManagementService menuManagementService;
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

    @PostMapping("/users/{userId}/update-info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUserInfo(@PathVariable Long userId,
                                                                @RequestBody Map<String, String> request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            String nickname = request.get("nickname");
            String name = request.get("name");

            if (nickname != null && !nickname.trim().isEmpty()) {
                user.setNickname(nickname.trim());
            }
            if (name != null && !name.trim().isEmpty()) {
                user.setName(name.trim());
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "사용자 정보가 수정되었습니다.",
                "nickname", user.getNickname(),
                "name", user.getName() != null ? user.getName() : ""
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

    /**
     * 카테고리 관리 페이지
     */
    @GetMapping("/categories")
    public String categories(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<com.board.entity.Category> categories = categoryService.getAllCategories();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("categories", categories);

        return "admin/categories";
    }

    /**
     * 카테고리 생성 API
     */
    @PostMapping("/categories")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Integer displayOrder = request.get("displayOrder") != null ?
                    Integer.parseInt(request.get("displayOrder").toString()) : 0;

            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("카테고리 이름은 필수입니다.");
            }

            com.board.entity.Category category = categoryService.createCategory(name.trim(), description, displayOrder);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "카테고리가 생성되었습니다.",
                "category", category
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 카테고리 수정 API
     */
    @PutMapping("/categories/{categoryId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Integer displayOrder = request.get("displayOrder") != null ?
                    Integer.parseInt(request.get("displayOrder").toString()) : null;
            Boolean enabled = request.get("enabled") != null ?
                    Boolean.parseBoolean(request.get("enabled").toString()) : null;

            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("카테고리 이름은 필수입니다.");
            }

            com.board.entity.Category category = categoryService.updateCategory(
                    categoryId, name.trim(), description, displayOrder, enabled);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "카테고리가 수정되었습니다.",
                "category", category
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 카테고리 삭제 API
     */
    @DeleteMapping("/categories/{categoryId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "카테고리가 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 카테고리 활성화/비활성화 토글 API
     */
    @PostMapping("/categories/{categoryId}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleCategoryStatus(@PathVariable Long categoryId) {
        try {
            com.board.entity.Category category = categoryService.toggleCategoryStatus(categoryId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", category.getEnabled() ? "카테고리가 활성화되었습니다." : "카테고리가 비활성화되었습니다.",
                "enabled", category.getEnabled()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // =============== 게시글 대량 관리 API ===============

    /**
     * 게시글 대량 삭제 (소프트 삭제)
     */
    @PostMapping("/boards/bulk-delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bulkDeleteBoards(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> boardIds = request.get("boardIds");
            if (boardIds == null || boardIds.isEmpty()) {
                throw new RuntimeException("삭제할 게시글을 선택해주세요.");
            }
            boardService.bulkSoftDelete(boardIds);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", boardIds.size() + "개의 게시글이 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 게시글 대량 숨김 처리
     */
    @PostMapping("/boards/bulk-hide")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bulkHideBoards(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> boardIds = request.get("boardIds");
            if (boardIds == null || boardIds.isEmpty()) {
                throw new RuntimeException("숨김 처리할 게시글을 선택해주세요.");
            }
            boardService.bulkHide(boardIds);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", boardIds.size() + "개의 게시글이 숨김 처리되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 게시글 대량 복구
     */
    @PostMapping("/boards/bulk-restore")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bulkRestoreBoards(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> boardIds = request.get("boardIds");
            if (boardIds == null || boardIds.isEmpty()) {
                throw new RuntimeException("복구할 게시글을 선택해주세요.");
            }
            boardService.bulkRestore(boardIds);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", boardIds.size() + "개의 게시글이 복구되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 게시글 완전 삭제 (하드 삭제)
     */
    @PostMapping("/boards/bulk-hard-delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bulkHardDeleteBoards(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> boardIds = request.get("boardIds");
            if (boardIds == null || boardIds.isEmpty()) {
                throw new RuntimeException("완전 삭제할 게시글을 선택해주세요.");
            }
            boardService.bulkHardDelete(boardIds);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", boardIds.size() + "개의 게시글이 완전히 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 게시글 상태 변경
     */
    @PostMapping("/boards/{boardId}/update-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBoardStatus(
            @PathVariable Long boardId,
            @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            BoardStatus status = BoardStatus.valueOf(statusStr);
            boardService.updateBoardStatus(boardId, status);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "게시글 상태가 변경되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 게시글 상단 고정
     */
    @PostMapping("/boards/{boardId}/pin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> pinBoard(
            @PathVariable Long boardId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pinnedUntil) {
        try {
            boardService.pinBoard(boardId, pinnedUntil);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "게시글이 상단에 고정되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 게시글 고정 해제
     */
    @PostMapping("/boards/{boardId}/unpin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unpinBoard(@PathVariable Long boardId) {
        try {
            boardService.unpinBoard(boardId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "게시글 고정이 해제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 게시글 중요 표시 토글
     */
    @PostMapping("/boards/{boardId}/toggle-important")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleBoardImportant(@PathVariable Long boardId) {
        try {
            Board board = boardService.toggleImportant(boardId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", board.getIsImportant() ? "중요 표시되었습니다." : "중요 표시가 해제되었습니다.",
                "isImportant", board.getIsImportant()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // =============== 금지어 관리 API ===============

    /**
     * 금지어 관리 페이지
     */
    @GetMapping("/banned-words")
    public String bannedWords(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<BannedWord> bannedWords = bannedWordService.getAllBannedWords();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("bannedWords", bannedWords);

        return "admin/banned-words";
    }

    /**
     * 금지어 생성 API
     */
    @PostMapping("/banned-words")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createBannedWord(@RequestBody Map<String, Object> request) {
        try {
            String word = (String) request.get("word");
            String description = (String) request.get("description");
            Boolean isRegex = request.get("isRegex") != null ?
                    Boolean.parseBoolean(request.get("isRegex").toString()) : false;
            String actionStr = (String) request.get("action");
            BannedWordAction action = actionStr != null ? BannedWordAction.valueOf(actionStr) : BannedWordAction.BLOCK;

            if (word == null || word.trim().isEmpty()) {
                throw new RuntimeException("금지어는 필수입니다.");
            }

            BannedWord bannedWord = bannedWordService.createBannedWord(word.trim(), description, isRegex, action);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "금지어가 추가되었습니다.",
                "bannedWord", bannedWord
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 금지어 수정 API
     */
    @PutMapping("/banned-words/{bannedWordId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBannedWord(
            @PathVariable Long bannedWordId,
            @RequestBody Map<String, Object> request) {
        try {
            String word = (String) request.get("word");
            String description = (String) request.get("description");
            Boolean isRegex = request.get("isRegex") != null ?
                    Boolean.parseBoolean(request.get("isRegex").toString()) : null;
            String actionStr = (String) request.get("action");
            BannedWordAction action = actionStr != null ? BannedWordAction.valueOf(actionStr) : null;
            Boolean enabled = request.get("enabled") != null ?
                    Boolean.parseBoolean(request.get("enabled").toString()) : null;

            if (word == null || word.trim().isEmpty()) {
                throw new RuntimeException("금지어는 필수입니다.");
            }

            BannedWord bannedWord = bannedWordService.updateBannedWord(
                    bannedWordId, word.trim(), description, isRegex, action, enabled);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "금지어가 수정되었습니다.",
                "bannedWord", bannedWord
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 금지어 삭제 API
     */
    @DeleteMapping("/banned-words/{bannedWordId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteBannedWord(@PathVariable Long bannedWordId) {
        try {
            bannedWordService.deleteBannedWord(bannedWordId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "금지어가 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 금지어 활성화/비활성화 토글 API
     */
    @PostMapping("/banned-words/{bannedWordId}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleBannedWordStatus(@PathVariable Long bannedWordId) {
        try {
            BannedWord bannedWord = bannedWordService.toggleBannedWordStatus(bannedWordId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", bannedWord.getEnabled() ? "금지어가 활성화되었습니다." : "금지어가 비활성화되었습니다.",
                "enabled", bannedWord.getEnabled()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // =============== 파일 스토리지 관리 API ===============

    /**
     * 파일 스토리지 관리 페이지
     */
    @GetMapping("/files")
    public String files(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 전체 통계
        Map<String, Object> storageStats = fileStorageService.getStorageStatistics();

        // 모든 파일 목록
        List<Attachment> allFiles = fileStorageService.getAllFiles();

        // 대용량 파일 TOP 10
        List<Attachment> largestFiles = fileStorageService.getTopLargestFiles(10);

        // 사용자별 통계
        List<Map<String, Object>> userStats = fileStorageService.getUserStorageStatistics();

        // 파일 타입별 통계
        List<Map<String, Object>> typeStats = fileStorageService.getFileTypeStatistics();

        // 고아 파일 목록
        List<Attachment> orphanedFiles = fileStorageService.getOrphanedFiles();

        // 최근 파일
        List<Attachment> recentFiles = fileStorageService.getRecentFiles(20);

        // DB와 디스크 동기화 상태
        Map<String, Object> syncStatus = fileStorageService.checkSyncStatus();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("storageStats", storageStats);
        model.addAttribute("allFiles", allFiles);
        model.addAttribute("largestFiles", largestFiles);
        model.addAttribute("userStats", userStats);
        model.addAttribute("typeStats", typeStats);
        model.addAttribute("orphanedFiles", orphanedFiles);
        model.addAttribute("recentFiles", recentFiles);
        model.addAttribute("syncStatus", syncStatus);

        return "admin/files";
    }

    /**
     * 파일 삭제 API
     */
    @DeleteMapping("/files/{fileId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable Long fileId) {
        try {
            fileStorageService.deleteFile(fileId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "파일이 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 여러 파일 일괄 삭제 API
     */
    @PostMapping("/files/bulk-delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bulkDeleteFiles(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> fileIds = request.get("fileIds");
            if (fileIds == null || fileIds.isEmpty()) {
                throw new RuntimeException("삭제할 파일을 선택해주세요.");
            }

            Map<String, Object> result = fileStorageService.deleteMultipleFiles(fileIds);
            result.put("success", true);
            result.put("message", result.get("deletedCount") + "개의 파일이 삭제되었습니다.");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 고아 파일 정리 API
     */
    @PostMapping("/files/cleanup-orphaned")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cleanupOrphanedFiles(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> result = fileStorageService.cleanupOldOrphanedFiles(days);
            result.put("success", true);
            result.put("message", result.get("deletedCount") + "개의 고아 파일이 정리되었습니다 (백업: " + result.get("backupCount") + "개)");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 스토리지 통계 API
     */
    @GetMapping("/files/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStorageStatistics() {
        try {
            Map<String, Object> stats = fileStorageService.getStorageStatistics();
            Map<String, Object> syncStatus = fileStorageService.checkSyncStatus();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", stats);
            response.put("syncStatus", syncStatus);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // =============== 역할(Role) 관리 API ===============

    /**
     * 역할 관리 페이지
     */
    @GetMapping("/roles")
    public String roles(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<com.board.entity.Role> roles = roleManagementService.getAllRoles();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("roles", roles);

        return "admin/roles";
    }

    /**
     * 모든 역할 조회 API
     */
    @GetMapping("/api/roles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        try {
            List<com.board.entity.Role> roles = roleManagementService.getAllRoles();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "roles", roles
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 역할 상세 조회 API
     */
    @GetMapping("/api/roles/{roleId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Long roleId) {
        try {
            com.board.entity.Role role = roleManagementService.getRoleById(roleId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "role", role
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 역할 생성 API
     */
    @PostMapping("/api/roles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String displayName = (String) request.get("displayName");
            String description = (String) request.get("description");
            Integer priority = request.get("priority") != null ?
                    Integer.parseInt(request.get("priority").toString()) : null;
            Boolean isSystem = request.get("isSystem") != null ?
                    Boolean.parseBoolean(request.get("isSystem").toString()) : false;

            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("역할명은 필수입니다.");
            }
            if (displayName == null || displayName.trim().isEmpty()) {
                throw new RuntimeException("표시명은 필수입니다.");
            }

            com.board.entity.Role role = roleManagementService.createRole(
                    name.trim(), displayName.trim(), description, priority, isSystem);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "역할이 생성되었습니다.",
                "role", role
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 역할 수정 API
     */
    @PutMapping("/api/roles/{roleId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long roleId,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String displayName = (String) request.get("displayName");
            String description = (String) request.get("description");
            Integer priority = request.get("priority") != null ?
                    Integer.parseInt(request.get("priority").toString()) : null;
            Boolean enabled = request.get("enabled") != null ?
                    Boolean.parseBoolean(request.get("enabled").toString()) : null;

            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("역할명은 필수입니다.");
            }
            if (displayName == null || displayName.trim().isEmpty()) {
                throw new RuntimeException("표시명은 필수입니다.");
            }

            com.board.entity.Role role = roleManagementService.updateRole(
                    roleId, name.trim(), displayName.trim(), description, priority, enabled);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "역할이 수정되었습니다.",
                "role", role
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 역할 삭제 API
     */
    @DeleteMapping("/api/roles/{roleId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Long roleId) {
        try {
            roleManagementService.deleteRole(roleId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "역할이 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 역할 활성화/비활성화 토글 API
     */
    @PostMapping("/api/roles/{roleId}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleRoleStatus(@PathVariable Long roleId) {
        try {
            com.board.entity.Role role = roleManagementService.toggleRoleStatus(roleId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", role.getEnabled() ? "역할이 활성화되었습니다." : "역할이 비활성화되었습니다.",
                "enabled", role.getEnabled()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // =============== 메뉴(Menu) 관리 API ===============

    /**
     * 메뉴 관리 페이지
     */
    @GetMapping("/menus")
    public String menus(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<Map<String, Object>> menuTree = menuManagementService.getMenuTree();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("menuTree", menuTree);

        return "admin/menus";
    }

    /**
     * 메뉴 트리 조회 API
     */
    @GetMapping("/api/menus/tree")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMenuTree() {
        try {
            List<Map<String, Object>> menuTree = menuManagementService.getMenuTree();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "menuTree", menuTree
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 모든 메뉴 조회 API (평면 구조)
     */
    @GetMapping("/api/menus")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllMenus() {
        try {
            List<com.board.entity.Menu> menus = menuManagementService.getAllMenus();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "menus", menus
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 최상위 메뉴 조회 API
     */
    @GetMapping("/api/menus/top-level")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTopLevelMenus() {
        try {
            List<com.board.entity.Menu> menus = menuManagementService.getTopLevelMenus();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "menus", menus
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 메뉴 상세 조회 API
     */
    @GetMapping("/api/menus/{menuId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMenuById(@PathVariable Long menuId) {
        try {
            com.board.entity.Menu menu = menuManagementService.getMenuById(menuId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "menu", menu
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 특정 부모의 하위 메뉴 조회 API
     */
    @GetMapping("/api/menus/{parentId}/children")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChildMenus(@PathVariable Long parentId) {
        try {
            List<com.board.entity.Menu> menus = menuManagementService.getChildMenus(parentId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "menus", menus
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 메뉴 생성 API
     */
    @PostMapping("/api/menus")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createMenu(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long parentId = request.get("parentId") != null ?
                    Long.parseLong(request.get("parentId").toString()) : null;
            String url = (String) request.get("url");
            String icon = (String) request.get("icon");
            Integer displayOrder = request.get("displayOrder") != null ?
                    Integer.parseInt(request.get("displayOrder").toString()) : null;
            String menuType = (String) request.get("menuType");

            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("메뉴명은 필수입니다.");
            }

            com.board.entity.Menu menu = menuManagementService.createMenu(
                    name.trim(), description, parentId, url, icon, displayOrder, menuType);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "메뉴가 생성되었습니다.",
                "menu", menu
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 메뉴 수정 API
     */
    @PutMapping("/api/menus/{menuId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateMenu(
            @PathVariable Long menuId,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            Long parentId = request.get("parentId") != null ?
                    Long.parseLong(request.get("parentId").toString()) : null;
            String url = (String) request.get("url");
            String icon = (String) request.get("icon");
            Integer displayOrder = request.get("displayOrder") != null ?
                    Integer.parseInt(request.get("displayOrder").toString()) : null;
            Boolean enabled = request.get("enabled") != null ?
                    Boolean.parseBoolean(request.get("enabled").toString()) : null;

            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("메뉴명은 필수입니다.");
            }

            com.board.entity.Menu menu = menuManagementService.updateMenu(
                    menuId, name.trim(), description, parentId, url, icon, displayOrder, enabled);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "메뉴가 수정되었습니다.",
                "menu", menu
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 메뉴 삭제 API
     */
    @DeleteMapping("/api/menus/{menuId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteMenu(@PathVariable Long menuId) {
        try {
            menuManagementService.deleteMenu(menuId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "메뉴가 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 메뉴 활성화/비활성화 토글 API
     */
    @PostMapping("/api/menus/{menuId}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleMenuStatus(@PathVariable Long menuId) {
        try {
            com.board.entity.Menu menu = menuManagementService.toggleMenuStatus(menuId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", menu.getEnabled() ? "메뉴가 활성화되었습니다." : "메뉴가 비활성화되었습니다.",
                "enabled", menu.getEnabled()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // =============== 권한 매핑(Role-Menu Permission) 관리 API ===============

    /**
     * 권한 매핑 관리 페이지
     */
    @GetMapping("/role-permissions")
    public String rolePermissions(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<com.board.entity.Role> roles = roleManagementService.getAllRoles();
        List<Map<String, Object>> menuTree = menuManagementService.getMenuTree();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("roles", roles);
        model.addAttribute("menuTree", menuTree);

        return "admin/role-permissions";
    }

    /**
     * 특정 역할의 권한 조회 API
     */
    @GetMapping("/api/role-permissions/{roleId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRolePermissions(@PathVariable Long roleId) {
        try {
            List<com.board.entity.RoleMenuPermission> permissions =
                    roleManagementService.getRolePermissions(roleId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "permissions", permissions
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 특정 역할의 접근 가능한 메뉴 트리 조회 API
     */
    @GetMapping("/api/role-permissions/{roleId}/menu-tree")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoleMenuTree(@PathVariable Long roleId) {
        try {
            List<Map<String, Object>> menuTree = menuManagementService.getAccessibleMenuTree(roleId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "menuTree", menuTree
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 역할에 메뉴 권한 부여 API
     */
    @PostMapping("/api/role-permissions/{roleId}/grant")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> grantMenuPermission(
            @PathVariable Long roleId,
            @RequestBody Map<String, Object> request) {
        try {
            Long menuId = Long.parseLong(request.get("menuId").toString());
            Boolean canRead = request.get("canRead") != null ?
                    Boolean.parseBoolean(request.get("canRead").toString()) : true;
            Boolean canWrite = request.get("canWrite") != null ?
                    Boolean.parseBoolean(request.get("canWrite").toString()) : false;
            Boolean canDelete = request.get("canDelete") != null ?
                    Boolean.parseBoolean(request.get("canDelete").toString()) : false;

            com.board.entity.RoleMenuPermission permission =
                    roleManagementService.grantMenuPermission(roleId, menuId, canRead, canWrite, canDelete);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "권한이 부여되었습니다.",
                "permission", permission
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 역할의 메뉴 권한 제거 API
     */
    @DeleteMapping("/api/role-permissions/{roleId}/revoke/{menuId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> revokeMenuPermission(
            @PathVariable Long roleId,
            @PathVariable Long menuId) {
        try {
            roleManagementService.revokeMenuPermission(roleId, menuId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "권한이 제거되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 역할의 모든 메뉴 권한 일괄 설정 API
     */
    @PostMapping("/api/role-permissions/{roleId}/bulk-update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bulkUpdatePermissions(
            @PathVariable Long roleId,
            @RequestBody Map<String, List<Map<String, Object>>> request) {
        try {
            List<Map<String, Object>> permissions = request.get("permissions");
            if (permissions == null) {
                throw new RuntimeException("권한 목록이 필요합니다.");
            }

            Map<String, Object> result = roleManagementService.bulkUpdatePermissions(roleId, permissions);
            result.put("success", true);
            result.put("message", result.get("savedCount") + "개의 권한이 설정되었습니다.");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 기본 역할 및 메뉴 초기화 API
     */
    @PostMapping("/api/rbac/initialize")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> initializeRBAC() {
        try {
            roleManagementService.initializeDefaultRoles();
            menuManagementService.initializeDefaultMenus();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "기본 역할 및 메뉴가 초기화되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}

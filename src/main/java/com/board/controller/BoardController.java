package com.board.controller;

import com.board.dto.response.ApiResponse;
import com.board.dto.response.FileUploadResponse;
import com.board.entity.Attachment;
import com.board.entity.BannedWord;
import com.board.entity.Board;
import com.board.entity.Comment;
import com.board.entity.User;
import com.board.enums.BannedWordAction;
import com.board.enums.BoardStatus;
import com.board.enums.TargetType;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.service.BannedWordService;
import com.board.service.BoardService;
import com.board.service.BookmarkService;
import com.board.service.CommentService;
import com.board.service.FileUploadService;
import com.board.service.LikeService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import com.board.util.CurrentUser;
import com.board.util.FileUploadHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final FileUploadService fileUploadService;
    private final FileUploadHelper fileUploadHelper;
    private final BookmarkService bookmarkService;
    private final BannedWordService bannedWordService;
    private final com.board.service.CategoryService categoryService;
    private final com.board.service.HashtagService hashtagService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // Get paged boards
        Page<Board> boardPage = boardService.getAllBoards(pageable);

        // Get categories for filter
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "board/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer minViews,
            @RequestParam(required = false) Integer maxViews,
            @RequestParam(required = false) Integer minLikes,
            @RequestParam(required = false) Integer maxLikes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // Parse date strings to LocalDateTime
        java.time.LocalDateTime startDateTime = null;
        java.time.LocalDateTime endDateTime = null;

        if (startDate != null && !startDate.isEmpty()) {
            startDateTime = java.time.LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            endDateTime = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
        }

        // Perform advanced search
        Page<Board> boardPage = boardService.advancedSearch(
                searchType,
                keyword,
                categoryId,
                startDateTime,
                endDateTime,
                minViews,
                maxViews,
                minLikes,
                maxLikes,
                pageable
        );

        // Get categories for filter
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        // Pass search parameters back to view
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("minViews", minViews);
        model.addAttribute("maxViews", maxViews);
        model.addAttribute("minLikes", minLikes);
        model.addAttribute("maxLikes", maxLikes);

        return "board/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, @CurrentUser User currentUser, Model model) {
        Board board = boardService.increaseViewCount(id);

        // Get comments tree
        List<Comment> comments = commentService.getCommentsTreeByBoardId(id);

        // Get attachments
        List<Attachment> attachments = fileUploadService.getAttachmentsByBoardId(id);

        // Check if current user liked the post (하위 호환)
        boolean isPostLiked = currentUser != null &&
                              likeService.isLiked(TargetType.POST, id, currentUser.getId());

        // === 새로운 반응 시스템 데이터 ===
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        // 사용자의 현재 반응 조회
        com.board.enums.ReactionType userPostReaction = likeService.getUserReaction(
            TargetType.POST, id, currentUserId
        );

        // 반응 카운트 조회
        java.util.Map<String, Integer> reactionCounts = likeService.getReactionCounts(TargetType.POST, id);

        // 총 반응 수
        int totalReactionCount = reactionCounts.values().stream()
            .mapToInt(Integer::intValue).sum();

        // 댓글별 반응 처리
        for (Comment comment : comments) {
            com.board.enums.ReactionType userCommentReaction = likeService.getUserReaction(
                TargetType.COMMENT, comment.getId(), currentUserId
            );
            comment.setUserReaction(userCommentReaction);
        }

        // Check if current user bookmarked the post
        boolean isBookmarked = currentUser != null &&
                               bookmarkService.isBookmarked(currentUser.getId(), id);

        // Get bookmark count
        long bookmarkCount = bookmarkService.getBookmarkCount(id);

        // Get hashtags
        List<com.board.entity.Hashtag> hashtags = hashtagService.getBoardHashtags(id);

        // Get topics - TEMPORARILY DISABLED
        // List<com.board.entity.Topic> boardTopics = boardTopicService.getBoardTopics(id);
        // List<String> topicPaths = boardTopicService.getBoardTopicPaths(id);

        model.addAttribute("board", board);
        model.addAttribute("isOwner", currentUser != null && boardService.isOwner(board, currentUser));
        model.addAttribute("comments", comments);
        model.addAttribute("attachments", attachments);
        model.addAttribute("isPostLiked", isPostLiked);  // 하위 호환용
        model.addAttribute("userPostReaction", userPostReaction);  // 새로운 반응 시스템
        model.addAttribute("totalReactionCount", totalReactionCount);
        model.addAttribute("isBookmarked", isBookmarked);
        model.addAttribute("bookmarkCount", bookmarkCount);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("hashtags", hashtags);
        // model.addAttribute("boardTopics", boardTopics);
        // model.addAttribute("topicPaths", topicPaths);

        return "board/view";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("board", Board.builder().build());
        return "board/form";
    }

    @PostMapping
    public String create(@ModelAttribute Board board,
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        @RequestParam(value = "topicIds", required = false) List<Long> topicIds,
                        RedirectAttributes redirectAttributes) {
        // 금지어 검사
        String combinedText = board.getTitle() + " " + board.getContent();
        BannedWord bannedWord = bannedWordService.checkForBannedWords(combinedText);

        if (bannedWord != null) {
            if (bannedWord.getAction() == BannedWordAction.BLOCK) {
                // 차단 조치: 게시글 작성 차단
                redirectAttributes.addFlashAttribute("error",
                    "금지어가 포함되어 있어 게시글을 작성할 수 없습니다: " + bannedWord.getWord());
                return "redirect:/board/new";
            } else if (bannedWord.getAction() == BannedWordAction.PENDING) {
                // 승인 대기 조치: 게시글 상태를 PENDING으로 설정
                board.setStatus(BoardStatus.PENDING);
                redirectAttributes.addFlashAttribute("info",
                    "게시글이 관리자 검토 대기 상태로 등록되었습니다.");
            }
        }

        // 금지어가 없거나 승인 대기 상태로 게시글 생성
        Board savedBoard = boardService.createBoard(board);

        // 파일 업로드
        fileUploadHelper.handleFileUploads(files, savedBoard.getId());

        // 토픽 연결 - TEMPORARILY DISABLED
        // if (topicIds != null && !topicIds.isEmpty()) {
        //     try {
        //         boardTopicService.updateBoardTopics(savedBoard.getId(), topicIds);
        //     } catch (Exception e) {
        //         System.err.println("Topic linking failed: " + e.getMessage());
        //     }
        // }

        return "redirect:/board/" + savedBoard.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Board board = boardService.getBoardById(id);
        List<Attachment> attachments = fileUploadService.getAttachmentsByBoardId(id);

        model.addAttribute("board", board);
        model.addAttribute("attachments", attachments);
        return "board/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                        @ModelAttribute Board board,
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        @RequestParam(value = "topicIds", required = false) List<Long> topicIds,
                        RedirectAttributes redirectAttributes) {
        // 금지어 검사
        String combinedText = board.getTitle() + " " + board.getContent();
        BannedWord bannedWord = bannedWordService.checkForBannedWords(combinedText);

        if (bannedWord != null) {
            if (bannedWord.getAction() == BannedWordAction.BLOCK) {
                // 차단 조치: 게시글 수정 차단
                redirectAttributes.addFlashAttribute("error",
                    "금지어가 포함되어 있어 게시글을 수정할 수 없습니다: " + bannedWord.getWord());
                return "redirect:/board/" + id + "/edit";
            } else if (bannedWord.getAction() == BannedWordAction.PENDING) {
                // 승인 대기 조치: 게시글 상태를 PENDING으로 변경
                Board existingBoard = boardService.getBoardById(id);
                existingBoard.setStatus(BoardStatus.PENDING);
                redirectAttributes.addFlashAttribute("info",
                    "게시글이 관리자 검토 대기 상태로 변경되었습니다.");
            }
        }

        boardService.updateBoard(id, board);

        // 새 파일 업로드
        fileUploadHelper.handleFileUploads(files, id);

        // 토픽 연결 업데이트 - TEMPORARILY DISABLED
        // if (topicIds != null) {
        //     try {
        //         boardTopicService.updateBoardTopics(id, topicIds);
        //     } catch (Exception e) {
        //         System.err.println("Topic update failed: " + e.getMessage());
        //     }
        // }

        return "redirect:/board/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return "redirect:/board";
    }

    /**
     * 파일 업로드 API
     */
    @PostMapping("/{boardId}/upload")
    @ResponseBody
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @PathVariable Long boardId,
            @RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = fileUploadService.uploadFile(file, boardId);
            FileUploadResponse fileResponse = FileUploadResponse.from(attachment);
            return ResponseEntity.ok(ApiResponse.success("파일이 업로드되었습니다.", fileResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 파일 삭제 API
     */
    @DeleteMapping("/attachment/{attachmentId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable Long attachmentId) {
        try {
            fileUploadService.deleteFile(attachmentId);
            return ResponseEntity.ok(ApiResponse.success("파일이 삭제되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 임시저장 목록 페이지
     */
    @GetMapping("/drafts")
    public String draftList(@CurrentUser User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        List<Board> drafts = boardService.getDraftsByUserId(currentUser.getId());
        model.addAttribute("drafts", drafts);
        return "board/drafts";
    }

    /**
     * 임시저장
     */
    @PostMapping("/draft")
    public String saveDraft(@ModelAttribute Board board,
                           @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        // 임시저장
        Board savedDraft = boardService.saveDraft(board);

        // 파일 업로드
        fileUploadHelper.handleFileUploads(files, savedDraft.getId());

        return "redirect:/board/drafts";
    }

    /**
     * 임시저장 게시글 발행
     */
    @PostMapping("/{id}/publish")
    public String publishDraft(@PathVariable Long id) {
        try {
            boardService.publishDraft(id);
            return "redirect:/board/" + id;
        } catch (Exception e) {
            return "redirect:/board/drafts?error=" + e.getMessage();
        }
    }

    /**
     * 임시저장 게시글 편집 폼
     */
    @GetMapping("/draft/{id}/edit")
    public String editDraft(@PathVariable Long id, @CurrentUser User currentUser, Model model) {
        Board board = boardService.getBoardById(id);

        // 권한 확인
        if (currentUser == null || !boardService.isOwner(board, currentUser)) {
            return "redirect:/board/drafts";
        }

        // 임시저장 상태 확인
        if (!board.getIsDraft()) {
            return "redirect:/board/" + id + "/edit";
        }

        // 첨부파일 조회
        List<Attachment> attachments = fileUploadService.getAttachmentsByBoardId(id);

        model.addAttribute("board", board);
        model.addAttribute("attachments", attachments);
        model.addAttribute("isDraft", true);
        return "board/form";
    }

    /**
     * 임시저장 게시글 업데이트
     */
    @PostMapping("/draft/{id}")
    public String updateDraft(@PathVariable Long id,
                              @ModelAttribute Board board,
                              @RequestParam(value = "files", required = false) List<MultipartFile> files,
                              @CurrentUser User currentUser) {
        Board existingBoard = boardService.getBoardById(id);

        // 권한 확인
        if (currentUser == null || !boardService.isOwner(existingBoard, currentUser)) {
            return "redirect:/board/drafts";
        }

        // 업데이트
        existingBoard.setTitle(board.getTitle());
        existingBoard.setContent(board.getContent());
        boardService.saveDraft(existingBoard);

        // 파일 업로드
        fileUploadHelper.handleFileUploads(files, id);

        return "redirect:/board/drafts";
    }

}

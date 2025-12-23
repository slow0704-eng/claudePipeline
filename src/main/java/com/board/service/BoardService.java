package com.board.service;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.enums.BoardStatus;
import com.board.repository.BoardRepository;
import com.board.repository.BookmarkRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final FileUploadService fileUploadService;
    private final BookmarkRepository bookmarkRepository;

    public Page<Board> getAllBoards(Pageable pageable) {
        // 임시저장 게시글 제외하고 조회
        return boardRepository.findByIsDraftFalse(pageable);
    }

    public List<Board> getAllBoards() {
        // 임시저장 게시글 제외하고 조회
        return boardRepository.findAllPublishedOrderByCreatedAtDesc();
    }

    public Board getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public Board increaseViewCount(Long id) {
        // viewCount만 증가시키는 쿼리 사용 (다른 필드에 영향 없음)
        boardRepository.incrementViewCount(id);
        // 업데이트된 board 반환
        return getBoardById(id);
    }

    public Board createBoard(Board board) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null) {
            board.setUserId(currentUser.getId());
            board.setNickname(currentUser.getNickname());
            board.setAuthor(currentUser.getNickname());
        }
        // 카운트 필드 명시적 초기화
        if (board.getViewCount() == null) {
            board.setViewCount(0);
        }
        if (board.getLikeCount() == null) {
            board.setLikeCount(0);
        }
        if (board.getCommentCount() == null) {
            board.setCommentCount(0);
        }
        return boardRepository.save(board);
    }

    public Board updateBoard(Long id, Board boardDetails) {
        Board board = getBoardById(id);

        // Check ownership
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null && !isOwner(board, currentUser)) {
            throw new RuntimeException("자신의 글만 수정할 수 있습니다.");
        }

        board.setTitle(boardDetails.getTitle());
        board.setContent(boardDetails.getContent());
        return boardRepository.save(board);
    }

    @Transactional
    public void deleteBoard(Long id) {
        Board board = getBoardById(id);

        // Check ownership
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null && !isOwner(board, currentUser)) {
            throw new RuntimeException("자신의 글만 삭제할 수 있습니다.");
        }

        // 첨부파일 삭제
        fileUploadService.deleteFilesByBoardId(id);

        // 북마크 삭제
        bookmarkRepository.deleteByBoardId(id);

        boardRepository.delete(board);
    }

    public boolean isOwner(Board board, User user) {
        if (board.getUserId() == null || user == null) {
            return false;
        }
        return board.getUserId().equals(user.getId());
    }

    public List<Board> getBoardsByUserId(Long userId) {
        return boardRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 임시저장
     */
    @Transactional
    public Board saveDraft(Board board) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null) {
            board.setUserId(currentUser.getId());
            board.setNickname(currentUser.getNickname());
            board.setAuthor(currentUser.getNickname());
        }
        // 카운트 필드 명시적 초기화
        if (board.getViewCount() == null) {
            board.setViewCount(0);
        }
        if (board.getLikeCount() == null) {
            board.setLikeCount(0);
        }
        if (board.getCommentCount() == null) {
            board.setCommentCount(0);
        }
        board.setIsDraft(true);
        return boardRepository.save(board);
    }

    /**
     * 사용자의 임시저장 목록 조회
     */
    public List<Board> getDraftsByUserId(Long userId) {
        return boardRepository.findByUserIdAndIsDraftTrueOrderByUpdatedAtDesc(userId);
    }

    /**
     * 사용자의 임시저장 개수 조회
     */
    public long getDraftCountByUserId(Long userId) {
        return boardRepository.countByUserIdAndIsDraftTrue(userId);
    }

    /**
     * 임시저장 게시글 발행
     */
    @Transactional
    public Board publishDraft(Long boardId) {
        Board board = getBoardById(boardId);

        // 권한 확인
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null && !isOwner(board, currentUser)) {
            throw new RuntimeException("자신의 글만 발행할 수 있습니다.");
        }

        // 임시저장 상태 확인
        if (!board.getIsDraft()) {
            throw new RuntimeException("이미 발행된 게시글입니다.");
        }

        board.setIsDraft(false);
        return boardRepository.save(board);
    }

    /**
     * 30일 이상 지난 임시저장 게시글 삭제
     */
    @Transactional
    public void deleteOldDrafts() {
        java.time.LocalDateTime cutoffDate = java.time.LocalDateTime.now().minusDays(30);
        List<Board> oldDrafts = boardRepository.findOldDrafts(cutoffDate);

        for (Board draft : oldDrafts) {
            // 첨부파일도 함께 삭제
            fileUploadService.deleteFilesByBoardId(draft.getId());
            boardRepository.delete(draft);
        }
    }

    // =============== 대량 관리 기능 ===============

    /**
     * 여러 게시글의 상태를 일괄 변경
     */
    @Transactional
    public void bulkUpdateStatus(List<Long> boardIds, BoardStatus newStatus) {
        if (boardIds == null || boardIds.isEmpty()) {
            throw new RuntimeException("게시글 ID 목록이 비어있습니다.");
        }

        for (Long boardId : boardIds) {
            Board board = getBoardById(boardId);
            board.setStatus(newStatus);
            boardRepository.save(board);
        }
    }

    /**
     * 여러 게시글을 일괄 삭제 (소프트 삭제)
     */
    @Transactional
    public void bulkSoftDelete(List<Long> boardIds) {
        bulkUpdateStatus(boardIds, BoardStatus.DELETED);
    }

    /**
     * 여러 게시글을 일괄 숨김 처리
     */
    @Transactional
    public void bulkHide(List<Long> boardIds) {
        bulkUpdateStatus(boardIds, BoardStatus.HIDDEN);
    }

    /**
     * 여러 게시글을 일괄 복구 (공개 상태로)
     */
    @Transactional
    public void bulkRestore(List<Long> boardIds) {
        bulkUpdateStatus(boardIds, BoardStatus.PUBLIC);
    }

    /**
     * 여러 게시글을 일괄 완전 삭제 (하드 삭제)
     */
    @Transactional
    public void bulkHardDelete(List<Long> boardIds) {
        if (boardIds == null || boardIds.isEmpty()) {
            throw new RuntimeException("게시글 ID 목록이 비어있습니다.");
        }

        for (Long boardId : boardIds) {
            Board board = getBoardById(boardId);
            // 첨부파일 삭제
            fileUploadService.deleteFilesByBoardId(boardId);
            // 북마크 삭제
            bookmarkRepository.deleteByBoardId(boardId);
            // 게시글 삭제
            boardRepository.delete(board);
        }
    }

    // =============== 검색/필터 기능 ===============

    /**
     * 복합 필터로 게시글 검색 (관리자용)
     */
    public Page<Board> searchBoardsWithFilters(
            BoardStatus status,
            Long categoryId,
            String keyword,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        return boardRepository.searchBoardsWithFilters(status, categoryId, keyword, startDate, endDate, pageable);
    }

    /**
     * 상태별 게시글 조회
     */
    public Page<Board> getBoardsByStatus(BoardStatus status, Pageable pageable) {
        return boardRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    /**
     * 카테고리별 게시글 조회
     */
    public Page<Board> getBoardsByCategory(Long categoryId, Pageable pageable) {
        return boardRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable);
    }

    /**
     * 상태와 카테고리로 게시글 조회
     */
    public Page<Board> getBoardsByStatusAndCategory(BoardStatus status, Long categoryId, Pageable pageable) {
        return boardRepository.findByStatusAndCategoryIdOrderByCreatedAtDesc(status, categoryId, pageable);
    }

    /**
     * 모든 게시글 조회 (관리자용)
     */
    public Page<Board> getAllBoardsForAdmin(Pageable pageable) {
        return boardRepository.findAllBoardsForAdmin(pageable);
    }

    // =============== 공지사항 관리 기능 ===============

    /**
     * 게시글을 상단 고정 (최대 3개)
     */
    @Transactional
    public Board pinBoard(Long boardId, LocalDateTime pinnedUntil) {
        // 현재 고정된 게시글 개수 확인
        long pinnedCount = boardRepository.countActivePinnedBoards(LocalDateTime.now());
        if (pinnedCount >= 3) {
            throw new RuntimeException("최대 3개까지만 고정할 수 있습니다. 기존 고정 게시글을 먼저 해제해주세요.");
        }

        Board board = getBoardById(boardId);
        board.setIsPinned(true);
        board.setPinnedUntil(pinnedUntil);
        return boardRepository.save(board);
    }

    /**
     * 게시글 고정 해제
     */
    @Transactional
    public Board unpinBoard(Long boardId) {
        Board board = getBoardById(boardId);
        board.setIsPinned(false);
        board.setPinnedUntil(null);
        return boardRepository.save(board);
    }

    /**
     * 중요 표시 설정/해제
     */
    @Transactional
    public Board toggleImportant(Long boardId) {
        Board board = getBoardById(boardId);
        board.setIsImportant(!board.getIsImportant());
        return boardRepository.save(board);
    }

    /**
     * 활성화된 고정 게시글 목록 조회
     */
    public List<Board> getActivePinnedBoards() {
        return boardRepository.findActivePinnedBoards(LocalDateTime.now());
    }

    /**
     * 고정 기간이 만료된 게시글 자동 해제
     */
    @Transactional
    public void unpinExpiredBoards() {
        List<Board> expiredBoards = boardRepository.findExpiredPinnedBoards(LocalDateTime.now());
        for (Board board : expiredBoards) {
            board.setIsPinned(false);
            board.setPinnedUntil(null);
            boardRepository.save(board);
        }
    }

    /**
     * 게시글 상태 변경 (관리자용)
     */
    @Transactional
    public Board updateBoardStatus(Long boardId, BoardStatus newStatus) {
        Board board = getBoardById(boardId);
        board.setStatus(newStatus);
        return boardRepository.save(board);
    }

    /**
     * 게시글 카테고리 변경
     */
    @Transactional
    public Board updateBoardCategory(Long boardId, Long categoryId) {
        Board board = getBoardById(boardId);
        board.setCategoryId(categoryId);
        return boardRepository.save(board);
    }
}

package com.board.service;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.enums.BoardStatus;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.exception.ValidationException;
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
    private final HashtagService hashtagService;

    @Transactional(readOnly = true)
    public Page<Board> getAllBoards(Pageable pageable) {
        // 임시저장 게시글 제외하고 조회
        return boardRepository.findByIsDraftFalse(pageable);
    }

    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        // 임시저장 게시글 제외하고 조회 + User Fetch Join (N+1 방지)
        return boardRepository.findAllPublishedWithUser();
    }

    /**
     * 게시글 조회 + User Fetch Join (N+1 문제 방지)
     * - 기본 조회 메서드에 Fetch Join 적용
     * - User 정보가 필요한 대부분의 경우에 사용
     */
    @Transactional(readOnly = true)
    public Board getBoardById(Long id) {
        return boardRepository.findByIdWithUser(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    /**
     * @deprecated Use getBoardById() instead
     * getBoardById()가 이미 User Fetch Join을 포함합니다
     */
    @Deprecated
    @Transactional(readOnly = true)
    public Board getBoardByIdWithUser(Long id) {
        return getBoardById(id);
    }

    @Transactional
    public Board increaseViewCount(Long id) {
        // viewCount만 증가시키는 쿼리 사용 (다른 필드에 영향 없음)
        boardRepository.incrementViewCount(id);
        // 업데이트된 board 반환
        return getBoardById(id);
    }

    @Transactional
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
        Board savedBoard = boardRepository.save(board);

        // 해시태그 자동 추출 및 저장 (임시저장이 아닌 경우에만)
        if (!Boolean.TRUE.equals(board.getIsDraft())) {
            String contentWithTitle = board.getTitle() + " " + board.getContent();
            hashtagService.updateBoardHashtags(savedBoard.getId(), contentWithTitle);
        }

        return savedBoard;
    }

    @Transactional
    public Board updateBoard(Long id, Board boardDetails) {
        Board board = getBoardById(id);

        // Check ownership
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null && !isOwner(board, currentUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS, "자신의 글만 수정할 수 있습니다.");
        }

        board.setTitle(boardDetails.getTitle());
        board.setContent(boardDetails.getContent());
        Board updatedBoard = boardRepository.save(board);

        // 해시태그 업데이트 (임시저장이 아닌 경우에만)
        if (!Boolean.TRUE.equals(board.getIsDraft())) {
            String contentWithTitle = board.getTitle() + " " + board.getContent();
            hashtagService.updateBoardHashtags(updatedBoard.getId(), contentWithTitle);
        }

        return updatedBoard;
    }

    @Transactional
    public void deleteBoard(Long id) {
        Board board = getBoardById(id);

        // Check ownership
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null && !isOwner(board, currentUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS, "자신의 글만 삭제할 수 있습니다.");
        }

        // 첨부파일 삭제
        fileUploadService.deleteFilesByBoardId(id);

        // 북마크 삭제
        bookmarkRepository.deleteByBoardId(id);

        // 해시태그 관계 삭제
        hashtagService.removeAllHashtagsFromBoard(id);

        boardRepository.delete(board);
    }

    public boolean isOwner(Board board, User user) {
        if (board.getUserId() == null || user == null) {
            return false;
        }
        return board.getUserId().equals(user.getId());
    }

    /**
     * 사용자별 게시글 조회 + User Fetch Join (N+1 방지)
     */
    @Transactional(readOnly = true)
    public List<Board> getBoardsByUserId(Long userId) {
        return boardRepository.findByUserIdWithUser(userId);
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
    @Transactional(readOnly = true)
    public List<Board> getDraftsByUserId(Long userId) {
        return boardRepository.findByUserIdAndIsDraftTrueOrderByUpdatedAtDescTitleAsc(userId);
    }

    /**
     * 사용자의 임시저장 개수 조회
     */
    @Transactional(readOnly = true)
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
            throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS, "자신의 글만 발행할 수 있습니다.");
        }

        // 임시저장 상태 확인
        if (!board.getIsDraft()) {
            throw new BusinessException(ErrorCode.BOARD_ALREADY_PUBLISHED);
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
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE, "게시글 ID 목록이 비어있습니다.");
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
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE, "게시글 ID 목록이 비어있습니다.");
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<Board> getBoardsByStatus(BoardStatus status, Pageable pageable) {
        return boardRepository.findByStatusOrderByCreatedAtDescTitleAsc(status, pageable);
    }

    /**
     * 카테고리별 게시글 조회
     */
    @Transactional(readOnly = true)
    public Page<Board> getBoardsByCategory(Long categoryId, Pageable pageable) {
        return boardRepository.findByCategoryIdOrderByCreatedAtDescTitleAsc(categoryId, pageable);
    }

    /**
     * 상태와 카테고리로 게시글 조회
     */
    @Transactional(readOnly = true)
    public Page<Board> getBoardsByStatusAndCategory(BoardStatus status, Long categoryId, Pageable pageable) {
        return boardRepository.findByStatusAndCategoryIdOrderByCreatedAtDescTitleAsc(status, categoryId, pageable);
    }

    /**
     * 모든 게시글 조회 (관리자용)
     */
    @Transactional(readOnly = true)
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
            throw new BusinessException(ErrorCode.MAX_PINNED_BOARDS_EXCEEDED, "기존 고정 게시글을 먼저 해제해주세요.");
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
    @Transactional(readOnly = true)
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

    // =============== 사용자용 고급 검색 기능 ===============

    /**
     * 고급 검색 (사용자용) - 여러 필터 조건으로 검색
     */
    @Transactional(readOnly = true)
    public Page<Board> advancedSearch(
            String searchType,
            String keyword,
            Long categoryId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer minViews,
            Integer maxViews,
            Integer minLikes,
            Integer maxLikes,
            Pageable pageable
    ) {
        // searchType에 따라 검색 방식 변경
        Page<Board> boards;

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType != null ? searchType : "all") {
                case "title":
                    boards = boardRepository.findByTitleContainingAndIsDraftFalse(keyword, pageable);
                    break;
                case "content":
                    boards = boardRepository.findByContentContainingAndIsDraftFalse(keyword, pageable);
                    break;
                case "author":
                    boards = boardRepository.findByNicknameContainingAndIsDraftFalse(keyword, pageable);
                    break;
                default: // "all"
                    boards = boardRepository.searchByKeyword(keyword, pageable);
                    break;
            }
        } else {
            // 키워드 없이 다른 필터만 사용하는 경우
            boards = boardRepository.findByIsDraftFalse(pageable);
        }

        // 추가 필터 적용 (메모리에서 필터링)
        if (categoryId != null || startDate != null || endDate != null ||
            minViews != null || maxViews != null || minLikes != null || maxLikes != null) {

            // 필터링이 필요한 경우 다시 쿼리 (더 효율적인 방법)
            return boardRepository.searchBoardsWithFilters(
                null, // status - 공개된 게시글만
                categoryId,
                keyword,
                startDate,
                endDate,
                pageable
            ).map(board -> {
                // 조회수 및 좋아요 수 필터링 (Repository 쿼리에 추가할 수도 있음)
                boolean passFilter = true;

                if (minViews != null && board.getViewCount() < minViews) {
                    passFilter = false;
                }
                if (maxViews != null && board.getViewCount() > maxViews) {
                    passFilter = false;
                }
                if (minLikes != null && board.getLikeCount() < minLikes) {
                    passFilter = false;
                }
                if (maxLikes != null && board.getLikeCount() > maxLikes) {
                    passFilter = false;
                }

                return passFilter ? board : null;
            });
        }

        return boards;
    }

    /**
     * 간단 검색 (키워드만)
     */
    @Transactional(readOnly = true)
    public Page<Board> simpleSearch(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return boardRepository.findByIsDraftFalse(pageable);
        }
        return boardRepository.searchByKeyword(keyword, pageable);
    }

    // ==================== 커뮤니티 게시판 통합 ====================

    /**
     * 커뮤니티 게시글 조회
     */
    @Transactional(readOnly = true)
    public Page<Board> getCommunityBoards(Long communityId, Pageable pageable) {
        return boardRepository.findByCommunityIdWithUser(communityId, pageable);
    }

    /**
     * 커뮤니티 + 카테고리별 게시글 조회
     */
    @Transactional(readOnly = true)
    public Page<Board> getCommunityBoardsByCategory(Long communityId, Long categoryId, Pageable pageable) {
        return boardRepository.findByCommunityIdAndCategoryIdWithUser(communityId, categoryId, pageable);
    }

    /**
     * 커뮤니티 게시글 생성
     * 멤버 여부 확인 필수
     */
    @Transactional
    public Board createCommunityBoard(Board board, Long communityId, Long communityCategoryId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        // 커뮤니티 설정
        board.setCommunityId(communityId);
        board.setCommunityCategoryId(communityCategoryId);

        // 작성자 정보 설정
        board.setUserId(currentUser.getId());
        board.setNickname(currentUser.getNickname());
        board.setAuthor(currentUser.getNickname());

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

        Board savedBoard = boardRepository.save(board);

        // 해시태그 자동 추출 및 저장 (임시저장이 아닌 경우에만)
        if (!Boolean.TRUE.equals(board.getIsDraft())) {
            String contentWithTitle = board.getTitle() + " " + board.getContent();
            hashtagService.updateBoardHashtags(savedBoard.getId(), contentWithTitle);
        }

        return savedBoard;
    }

    /**
     * 커뮤니티 게시글 수 조회
     */
    @Transactional(readOnly = true)
    public long getCommunityBoardCount(Long communityId) {
        return boardRepository.countByCommunityIdAndIsDraftFalse(communityId);
    }

    /**
     * 커뮤니티 최근 게시글 수 조회 (활동 통계용)
     */
    @Transactional(readOnly = true)
    public long getRecentCommunityBoardCount(Long communityId, LocalDateTime since) {
        return boardRepository.countByCommunityIdAndCreatedAtAfter(communityId, since);
    }
}

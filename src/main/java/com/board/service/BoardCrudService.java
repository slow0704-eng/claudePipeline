package com.board.service;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.BoardRepository;
import com.board.repository.BookmarkRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시글 CRUD 기본 작업을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class BoardCrudService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final HashtagService hashtagService;
    private final BookmarkRepository bookmarkRepository;

    @Transactional(readOnly = true)
    public Page<Board> getAllBoards(Pageable pageable) {
        return boardRepository.findByIsDraftFalse(pageable);
    }

    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        return boardRepository.findAllPublishedWithUser();
    }

    /**
     * 게시글 조회 + User Fetch Join (N+1 방지)
     */
    @Transactional(readOnly = true)
    public Board getBoardById(Long id) {
        return boardRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
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
        boardRepository.incrementViewCount(id);
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

        // 권한 체크
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null && !isOwner(board, currentUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }

        board.setTitle(boardDetails.getTitle());
        board.setContent(boardDetails.getContent());
        if (boardDetails.getCategoryId() != null) {
            board.setCategoryId(boardDetails.getCategoryId());
        }
        if (boardDetails.getStatus() != null) {
            board.setStatus(boardDetails.getStatus());
        }

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

        // 권한 체크
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null && !isOwner(board, currentUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }

        // 북마크 먼저 삭제 (cascade로 처리되지만 명시적으로)
        bookmarkRepository.deleteByBoardId(id);

        boardRepository.deleteById(id);
    }

    public boolean isOwner(Board board, User user) {
        if (board == null || user == null) {
            return false;
        }
        return board.getUserId().equals(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Board> getBoardsByUserId(Long userId) {
        return boardRepository.findByUserIdAndIsDraftFalseOrderByCreatedAtDesc(userId);
    }
}

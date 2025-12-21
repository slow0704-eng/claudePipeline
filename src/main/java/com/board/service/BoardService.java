package com.board.service;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.BookmarkRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

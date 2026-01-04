package com.board.service;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.BoardRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 임시저장 기능을 담당하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardDraftService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final HashtagService hashtagService;
    private final BoardCrudService boardCrudService;

    @Transactional
    public Board saveDraft(Board board) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null) {
            board.setUserId(currentUser.getId());
            board.setNickname(currentUser.getNickname());
            board.setAuthor(currentUser.getNickname());
        }

        board.setIsDraft(true);
        return boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public List<Board> getDraftsByUserId(Long userId) {
        return boardRepository.findByUserIdAndIsDraftTrueOrderByUpdatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getDraftCountByUserId(Long userId) {
        return boardRepository.countByUserIdAndIsDraftTrue(userId);
    }

    @Transactional
    public Board publishDraft(Long boardId) {
        Board draft = boardCrudService.getBoardById(boardId);

        // 권한 체크
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null && !boardCrudService.isOwner(draft, currentUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }

        if (!Boolean.TRUE.equals(draft.getIsDraft())) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND, "임시저장 게시글이 아닙니다.");
        }

        draft.setIsDraft(false);
        Board publishedBoard = boardRepository.save(draft);

        // 발행 시 해시태그 추출
        String contentWithTitle = draft.getTitle() + " " + draft.getContent();
        hashtagService.updateBoardHashtags(publishedBoard.getId(), contentWithTitle);

        return publishedBoard;
    }

    /**
     * 30일 이상 된 임시저장 게시글 자동 삭제
     * 매일 새벽 3시 실행
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteOldDrafts() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Board> oldDrafts = boardRepository.findByIsDraftTrueAndUpdatedAtBefore(thirtyDaysAgo);

        if (!oldDrafts.isEmpty()) {
            boardRepository.deleteAll(oldDrafts);
            log.info("Deleted {} old draft boards (older than 30 days)", oldDrafts.size());
        }
    }
}

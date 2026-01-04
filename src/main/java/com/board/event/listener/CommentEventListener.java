package com.board.event.listener;

import com.board.entity.Board;
import com.board.event.CommentCreatedEvent;
import com.board.repository.BoardRepository;
import com.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 댓글 이벤트 리스너
 *
 * 비동기로 실행되어 댓글 생성의 메인 트랜잭션과 분리됨
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventListener {

    private final NotificationService notificationService;
    private final BoardRepository boardRepository;

    /**
     * 댓글 생성 이벤트 처리
     *
     * - 트랜잭션 커밋 후 실행 (AFTER_COMMIT)
     * - 비동기 처리 (@Async)
     * - 별도 트랜잭션 (각 작업마다 독립적)
     */
    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(CommentCreatedEvent event) {
        log.info("Processing CommentCreatedEvent: commentId={}, boardId={}, isReply={}",
                event.getCommentId(), event.getBoardId(), event.isReply());

        try {
            // 1. 알림 생성 (별도 트랜잭션)
            createNotification(event);

            // 2. 댓글 수 증가 (별도 트랜잭션)
            incrementCommentCount(event);

            log.info("CommentCreatedEvent processed successfully: commentId={}",
                    event.getCommentId());
        } catch (Exception e) {
            // 이벤트 처리 실패해도 댓글 생성은 이미 완료됨
            log.error("Failed to process CommentCreatedEvent: commentId={}, error={}",
                     event.getCommentId(), e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void createNotification(CommentCreatedEvent event) {
        try {
            if (event.isReply()) {
                // 대댓글: 부모 댓글 작성자에게 알림
                notificationService.createCommentReplyNotification(
                        event.getBoardId(),
                        event.getCommentId(),
                        event.getParentCommentId(),
                        event.getAuthorId()
                );
            } else {
                // 일반 댓글: 게시글 작성자에게 알림
                notificationService.createCommentNotification(
                        event.getBoardId(),
                        event.getCommentId(),
                        event.getAuthorId()
                );
            }
        } catch (Exception e) {
            log.error("Failed to create notification for comment: {}", event.getCommentId(), e);
            // 알림 생성 실패는 무시 (중요하지 않은 부수 효과)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void incrementCommentCount(CommentCreatedEvent event) {
        try {
            Board board = boardRepository.findById(event.getBoardId()).orElse(null);
            if (board != null) {
                board.increaseCommentCount();
                boardRepository.save(board);
                log.debug("Comment count incremented for board: {}", event.getBoardId());
            }
        } catch (Exception e) {
            log.error("Failed to increment comment count for board: {}",
                     event.getBoardId(), e);
            // 카운트 업데이트 실패도 무시 (나중에 재계산 가능)
        }
    }
}

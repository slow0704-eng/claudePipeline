package com.board.event;

import lombok.Getter;

/**
 * 댓글 생성 이벤트
 *
 * 발행 시점: CommentService.createComment() 완료 후
 * 처리 내용:
 * - 알림 생성 (게시글 작성자에게)
 * - 댓글 수 증가 (게시글)
 * - 활동 로그 기록
 */
@Getter
public class CommentCreatedEvent extends DomainEvent {

    private final Long commentId;
    private final Long boardId;
    private final Long authorId;      // 댓글 작성자
    private final Long parentCommentId; // 대댓글인 경우
    private final String content;

    public CommentCreatedEvent(Long userId, Long commentId, Long boardId,
                              Long authorId, Long parentCommentId, String content) {
        super(userId);
        this.commentId = commentId;
        this.boardId = boardId;
        this.authorId = authorId;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    public boolean isReply() {
        return parentCommentId != null;
    }
}

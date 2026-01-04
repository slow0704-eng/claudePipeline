package com.board.event;

import com.board.enums.ReactionType;
import com.board.enums.TargetType;
import lombok.Getter;

/**
 * 반응(좋아요/리액션) 생성 이벤트
 *
 * 발행 시점: LikeService.toggleReaction() - 새로운 반응 생성 시
 * 처리 내용:
 * - 알림 생성 (게시글/댓글 작성자에게)
 * - 반응 카운트 업데이트 (JSONB)
 * - 인기도 점수 갱신
 */
@Getter
public class ReactionCreatedEvent extends DomainEvent {

    private final Long likeId;
    private final TargetType targetType;  // POST or COMMENT
    private final Long targetId;
    private final ReactionType reactionType;
    private final Long targetAuthorId;    // 게시글/댓글 작성자 ID

    public ReactionCreatedEvent(Long userId, Long likeId, TargetType targetType,
                               Long targetId, ReactionType reactionType, Long targetAuthorId) {
        super(userId);
        this.likeId = likeId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reactionType = reactionType;
        this.targetAuthorId = targetAuthorId;
    }

    public boolean isPostReaction() {
        return TargetType.POST.equals(targetType);
    }

    public boolean isCommentReaction() {
        return TargetType.COMMENT.equals(targetType);
    }
}

package com.board.dto.response;

import com.board.entity.Comment;
import com.board.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 댓글 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private Long boardId;
    private Long userId;
    private String nickname;
    private String content;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private Integer likeCount;
    private Map<String, Integer> reactionCounts;

    /**
     * 현재 사용자의 반응
     */
    private ReactionType userReaction;

    /**
     * 대댓글 목록
     */
    private List<CommentResponse> replies;

    /**
     * Entity -> Response DTO 변환
     */
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .boardId(comment.getBoardId())
                .userId(comment.getUserId())
                .nickname(comment.getNickname())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isDeleted(comment.getIsDeleted())
                .likeCount(comment.getLikeCount())
                .reactionCounts(comment.getReactionCountsMap())
                .userReaction(comment.getUserReaction())
                .replies(comment.getReplies() != null
                        ? comment.getReplies().stream()
                                .map(CommentResponse::from)
                                .collect(Collectors.toList())
                        : List.of())
                .build();
    }
}

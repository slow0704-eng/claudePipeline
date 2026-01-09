package com.board.dto.response;

import com.board.entity.Board;
import com.board.enums.BoardStatus;
import com.board.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 게시글 상세 조회 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailResponse {

    private Long id;
    private String title;
    private String content;
    private String author;
    private String nickname;
    private Long userId;
    private Long categoryId;
    private BoardStatus status;
    private Boolean isPinned;
    private Boolean isImportant;
    private LocalDateTime pinnedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private Integer likeCount;
    private Map<String, Integer> reactionCounts;
    private Integer commentCount;
    private Boolean isDraft;

    /**
     * 현재 사용자의 반응 (좋아요, 도움됨 등)
     */
    private ReactionType userReaction;

    /**
     * Entity -> Response DTO 변환
     */
    public static BoardDetailResponse from(Board board) {
        return BoardDetailResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .author(board.getAuthor())
                .nickname(board.getNickname())
                .userId(board.getUserId())
                .categoryId(board.getCategoryId())
                .status(board.getStatus())
                .isPinned(board.getIsPinned())
                .isImportant(board.getIsImportant())
                .pinnedUntil(board.getPinnedUntil())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .reactionCounts(board.getReactionCountsMap())
                .commentCount(board.getCommentCount())
                .isDraft(board.getIsDraft())
                .userReaction(board.getUserReaction())
                .build();
    }
}

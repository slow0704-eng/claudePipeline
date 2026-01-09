package com.board.dto.response;

import com.board.entity.Board;
import com.board.enums.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 목록 조회 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListResponse {

    private Long id;
    private String title;
    private String author;
    private String nickname;
    private Long userId;
    private Long categoryId;
    private BoardStatus status;
    private Boolean isPinned;
    private Boolean isImportant;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;

    /**
     * Entity -> Response DTO 변환
     */
    public static BoardListResponse from(Board board) {
        return BoardListResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .author(board.getAuthor())
                .nickname(board.getNickname())
                .userId(board.getUserId())
                .categoryId(board.getCategoryId())
                .status(board.getStatus())
                .isPinned(board.getIsPinned())
                .isImportant(board.getIsImportant())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                .build();
    }
}

package com.board.dto.request;

import com.board.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long boardId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = 1, max = 1000, message = "댓글은 1-1000자 이내여야 합니다.")
    private String content;

    /**
     * 대댓글인 경우, 부모 댓글 ID
     */
    private Long parentCommentId;

    /**
     * Request DTO -> Entity 변환
     */
    public Comment toEntity(Long userId, String nickname) {
        Comment comment = new Comment();
        comment.setBoardId(this.boardId);
        comment.setUserId(userId);
        comment.setNickname(nickname);
        comment.setContent(this.content);
        comment.setParentCommentId(this.parentCommentId);
        comment.setIsDeleted(false);
        return comment;
    }
}

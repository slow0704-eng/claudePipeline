package com.board.dto.request;

import com.board.entity.Board;
import com.board.enums.BoardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(min = 1, max = 10000, message = "내용은 1-10000자 이내여야 합니다.")
    private String content;

    private Long categoryId;

    @Builder.Default
    private BoardStatus status = BoardStatus.PUBLIC;

    @Builder.Default
    private Boolean isDraft = false;

    /**
     * Request DTO -> Entity 변환
     */
    public Board toEntity(String author, String nickname, Long userId) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .author(author)
                .nickname(nickname)
                .userId(userId)
                .categoryId(this.categoryId)
                .status(this.status)
                .isDraft(this.isDraft)
                .isPinned(false)
                .isImportant(false)
                .build();
    }
}

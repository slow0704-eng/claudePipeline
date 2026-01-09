package com.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 타임라인 아이템 DTO
 * - 게시글과 공유 정보를 통합하여 표현
 * - Facebook 뉴스피드 스타일 타임라인용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineItemDTO {

    /**
     * 아이템 타입: "POST" (일반 게시글) 또는 "SHARE" (공유된 게시글)
     */
    private String itemType;

    /**
     * 정렬용 타임스탬프 (POST는 createdAt, SHARE는 sharedAt 사용)
     */
    private LocalDateTime timestamp;

    // ========== 게시글 정보 ==========

    private Long boardId;
    private String title;
    private String content;
    private String author;          // 원본 게시글 작성자 닉네임
    private Long authorId;
    private LocalDateTime createdAt;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;

    // ========== 공유 정보 (itemType="SHARE"인 경우에만 사용) ==========

    private Long shareId;
    private String shareType;       // "SIMPLE" 또는 "QUOTE"
    private String quoteContent;    // 인용 공유 시 코멘트
    private String sharer;          // 공유한 사람 닉네임
    private Long sharerId;
    private LocalDateTime sharedAt; // 공유 일시
}

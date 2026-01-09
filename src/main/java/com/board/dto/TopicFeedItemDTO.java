package com.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 토픽 피드 아이템 DTO
 * - 팔로우한 토픽의 게시글 정보
 * - 공유된 게시글도 포함
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicFeedItemDTO {

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

    // ========== 토픽 정보 ==========

    /**
     * 이 게시글이 속한 토픽 목록
     */
    private List<TopicInfo> topics;

    /**
     * 주요 토픽 (첫 번째 토픽 또는 가장 상위 레벨 토픽)
     */
    private TopicInfo primaryTopic;

    // ========== 공유 정보 (itemType="SHARE"인 경우에만 사용) ==========

    private Long shareId;
    private String shareType;       // "SIMPLE" 또는 "QUOTE"
    private String quoteContent;    // 인용 공유 시 코멘트
    private String sharer;          // 공유한 사람 닉네임
    private Long sharerId;
    private LocalDateTime sharedAt; // 공유 일시

    /**
     * 토픽 정보 내부 클래스
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicInfo {
        private Long id;
        private String name;
        private String icon;
        private String color;
        private Integer level;
        private String fullPath;    // 전체 경로 (예: "개발 > Java > Spring")
    }
}

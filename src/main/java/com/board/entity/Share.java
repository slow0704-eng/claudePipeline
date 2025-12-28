package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 게시글 공유(Repost) 엔티티
 * - Twitter Retweet, Facebook Share와 유사한 기능
 */
@Entity
@Table(name = "share")
@Data
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 공유한 사용자 ID
     */
    @Column(nullable = false, name = "user_id")
    private Long userId;

    /**
     * 원본 게시글 ID
     */
    @Column(nullable = false, name = "board_id")
    private Long boardId;

    /**
     * 공유 시 추가한 코멘트 (Quote Repost)
     * - 선택 사항
     */
    @Column(length = 500, name = "quote_content")
    private String quoteContent;

    /**
     * 공유 유형
     * - SIMPLE: 단순 공유 (리트윗)
     * - QUOTE: 인용 공유 (코멘트 포함)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShareType shareType;

    /**
     * 공유 일시
     */
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 공유 유형 열거형
     */
    public enum ShareType {
        SIMPLE,  // 단순 공유
        QUOTE    // 인용 공유
    }
}

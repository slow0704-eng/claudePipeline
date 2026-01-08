package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 게시글 공유(Repost) 엔티티
 * - Twitter Retweet, Facebook Share와 유사한 기능
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "share", indexes = {
    @Index(name = "idx_share_user_created", columnList = "user_id, created_at DESC"),
    @Index(name = "idx_share_board", columnList = "board_id")
})
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 공유한 사용자 ID
     */
    @Column(nullable = false, name = "user_id", insertable = false, updatable = false)
    private Long userId;

    /**
     * 공유한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 원본 게시글 ID
     */
    @Column(nullable = false, name = "board_id", insertable = false, updatable = false)
    private Long boardId;

    /**
     * 원본 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getQuoteContent() {
        return quoteContent;
    }

    public void setQuoteContent(String quoteContent) {
        this.quoteContent = quoteContent;
    }

    public ShareType getShareType() {
        return shareType;
    }

    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 비즈니스 로직 메서드
    @PrePersist
    protected void onCreate() {
        if (shareType == null) {
            shareType = ShareType.SIMPLE;
        }
    }

    public boolean isQuoteShare() {
        return ShareType.QUOTE.equals(shareType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Share share = (Share) o;
        return Objects.equals(id, share.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Share{" +
                "id=" + id +
                ", userId=" + userId +
                ", boardId=" + boardId +
                ", quoteContent='" + quoteContent + '\'' +
                ", shareType=" + shareType +
                ", createdAt=" + createdAt +
                '}';
    }
}

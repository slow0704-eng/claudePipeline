package com.board.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 게시글-해시태그 중간 테이블
 * - 게시글과 해시태그의 다대다 관계 관리
 */
@Entity
@Table(name = "board_hashtag",
       indexes = {
           @Index(name = "idx_board_id", columnList = "board_id"),
           @Index(name = "idx_hashtag_id", columnList = "hashtag_id")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "hashtag_id"}))
public class BoardHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 ID
     */
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    /**
     * 해시태그 ID
     */
    @Column(name = "hashtag_id", nullable = false)
    private Long hashtagId;

    /**
     * 관계 생성 시간
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(Long hashtagId) {
        this.hashtagId = hashtagId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardHashtag that = (BoardHashtag) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BoardHashtag{" +
                "id=" + id +
                ", boardId=" + boardId +
                ", hashtagId=" + hashtagId +
                ", createdAt=" + createdAt +
                '}';
    }
}

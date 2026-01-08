package com.board.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 사용자-해시태그 팔로우 엔티티
 * - 사용자가 팔로우한 해시태그 관리
 */
@Entity
@Table(name = "user_hashtag_follow",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_hashtag_id", columnList = "hashtag_id")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "hashtag_id"}))
public class UserHashtagFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 팔로우한 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 팔로우된 해시태그 ID
     */
    @Column(name = "hashtag_id", nullable = false)
    private Long hashtagId;

    /**
     * 팔로우 시작 시간
     */
    @CreationTimestamp
    @Column(name = "followed_at", updatable = false)
    private LocalDateTime followedAt;

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

    public Long getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(Long hashtagId) {
        this.hashtagId = hashtagId;
    }

    public LocalDateTime getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(LocalDateTime followedAt) {
        this.followedAt = followedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserHashtagFollow that = (UserHashtagFollow) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserHashtagFollow{" +
                "id=" + id +
                ", userId=" + userId +
                ", hashtagId=" + hashtagId +
                ", followedAt=" + followedAt +
                '}';
    }
}

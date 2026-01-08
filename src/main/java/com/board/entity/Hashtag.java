package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 해시태그 엔티티
 * - 게시글에 사용되는 해시태그 관리
 * - 중복 방지를 위해 name을 unique로 설정
 */
@Entity
@Table(name = "hashtag",
       indexes = @Index(name = "idx_hashtag_name", columnList = "name"),
       uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 해시태그 이름 (# 제외)
     * 예: "java", "spring", "개발"
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 사용 횟수 (통계용)
     */
    @Column(name = "use_count", nullable = false)
    private Long useCount = 0L;

    /**
     * 최초 생성 시간
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 마지막 사용 시간
     */
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    /**
     * 해시태그 설명
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 금지 해시태그 여부 (관리자가 설정)
     */
    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned = false;

    /**
     * 병합된 해시태그 ID
     * 예: #JS → #JavaScript로 병합 시, JS의 mergedIntoId = JavaScript의 ID
     */
    @Column(name = "merged_into_id")
    private Long mergedIntoId;

    /**
     * 병합 시간
     */
    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUseCount() {
        return useCount;
    }

    public void setUseCount(Long useCount) {
        this.useCount = useCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }

    public Long getMergedIntoId() {
        return mergedIntoId;
    }

    public void setMergedIntoId(Long mergedIntoId) {
        this.mergedIntoId = mergedIntoId;
    }

    public LocalDateTime getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(LocalDateTime mergedAt) {
        this.mergedAt = mergedAt;
    }

    /**
     * 사용 횟수 증가
     */
    public void incrementUseCount() {
        this.useCount++;
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 사용 횟수 감소
     */
    public void decrementUseCount() {
        if (this.useCount > 0) {
            this.useCount--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hashtag hashtag = (Hashtag) o;
        return Objects.equals(id, hashtag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Hashtag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", useCount=" + useCount +
                ", createdAt=" + createdAt +
                ", lastUsedAt=" + lastUsedAt +
                ", description='" + description + '\'' +
                ", isBanned=" + isBanned +
                ", mergedIntoId=" + mergedIntoId +
                ", mergedAt=" + mergedAt +
                '}';
    }
}

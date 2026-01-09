package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "topic",
       indexes = {
           @Index(name = "idx_topic_name", columnList = "name"),
           @Index(name = "idx_topic_parent", columnList = "parent_id"),
           @Index(name = "idx_topic_level", columnList = "level"),
           @Index(name = "idx_topic_enabled", columnList = "enabled")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 계층 구조 필드 (Menu 패턴)
    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false)
    private Integer level = 0;  // 0, 1, or 2 (max 3 levels)

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    // 시각적 속성
    @Column(length = 50)
    private String icon;  // Emoji like "💻", "🎨", "📱"

    @Column(length = 7)
    private String color;  // Hex color like "#667eea"

    // 상태 필드 (Hashtag 패턴)
    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "merged_into_id")
    private Long mergedIntoId;

    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    // 사용 통계
    @Column(name = "usage_count", nullable = false)
    private Long usageCount = 0L;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void incrementUsageCount() {
        this.usageCount++;
        this.lastUsedAt = LocalDateTime.now();
    }

    public void decrementUsageCount() {
        if (this.usageCount > 0) {
            this.usageCount--;
        }
    }
}

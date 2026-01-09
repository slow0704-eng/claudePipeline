package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 주제(토픽) 엔티티
 * 게시글의 주제나 분류를 나타냅니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 주제명 (예: 기술, 일상, 여행 등)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 주제 설명
     */
    @Column(length = 500)
    private String description;

    /**
     * 사용 횟수 (인기도 측정)
     */
    @Column(nullable = false)
    private Integer usageCount = 0;

    /**
     * 활성화 여부
     */
    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 비즈니스 로직 메서드

    /**
     * 사용 횟수 증가
     */
    public void incrementUsageCount() {
        this.usageCount++;
    }

    /**
     * 사용 횟수 감소
     */
    public void decrementUsageCount() {
        if (this.usageCount > 0) {
            this.usageCount--;
        }
    }

    /**
     * 주제 활성화
     */
    public void activate() {
        this.active = true;
    }

    /**
     * 주제 비활성화
     */
    public void deactivate() {
        this.active = false;
    }

    @PrePersist
    protected void onCreate() {
        if (usageCount == null) usageCount = 0;
        if (active == null) active = true;
    }
}

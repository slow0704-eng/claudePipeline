package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 해시태그 엔티티
 * - 게시글에 사용되는 해시태그 관리
 * - 중복 방지를 위해 name을 unique로 설정
 */
@Entity
@Table(name = "hashtag",
       indexes = @Index(name = "idx_hashtag_name", columnList = "name"),
       uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString
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
}

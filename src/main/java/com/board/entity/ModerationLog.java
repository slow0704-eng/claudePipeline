package com.board.entity;

import com.board.enums.ModerationActionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 모더레이션 로그 엔티티
 */
@Entity
@Table(name = "moderation_logs",
       indexes = {
           @Index(name = "idx_ml_community_id", columnList = "community_id"),
           @Index(name = "idx_ml_moderator_id", columnList = "moderator_id"),
           @Index(name = "idx_ml_action_type", columnList = "action_type"),
           @Index(name = "idx_ml_created_at", columnList = "created_at")
       })
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"community", "moderator"})
public class ModerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_id", nullable = false, insertable = false, updatable = false)
    private Long communityId;

    /**
     * 모더레이션이 발생한 커뮤니티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Column(name = "moderator_id", nullable = false, insertable = false, updatable = false)
    private Long moderatorId;

    /**
     * 모더레이션을 수행한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ModerationActionType actionType;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType; // "MEMBER", "BOARD", "COMMENT"

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String details;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

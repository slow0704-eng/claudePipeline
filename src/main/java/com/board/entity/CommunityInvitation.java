package com.board.entity;

import com.board.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 커뮤니티 초대 엔티티
 */
@Entity
@Table(name = "community_invitations",
       indexes = {
           @Index(name = "idx_ci_community_id", columnList = "community_id"),
           @Index(name = "idx_ci_invited_user_id", columnList = "invited_user_id"),
           @Index(name = "idx_ci_status", columnList = "status"),
           @Index(name = "idx_ci_expires_at", columnList = "expires_at")
       })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"community", "invitedBy", "invitedUser"})
public class CommunityInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_id", nullable = false, insertable = false, updatable = false)
    private Long communityId;

    /**
     * 초대 대상 커뮤니티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Column(name = "invited_by_id", nullable = false, insertable = false, updatable = false)
    private Long invitedById;

    /**
     * 초대한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id")
    private User invitedBy;

    @Column(name = "invited_user_id", nullable = false, insertable = false, updatable = false)
    private Long invitedUserId;

    /**
     * 초대받은 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(length = 500)
    private String message;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = InvitationStatus.PENDING;
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(7); // 7일 후 만료
        }
    }

    /**
     * 초대 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}

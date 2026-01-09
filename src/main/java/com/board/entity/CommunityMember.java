package com.board.entity;

import com.board.enums.CommunityRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 커뮤니티 멤버 엔티티
 */
@Entity
@Table(name = "community_members",
       indexes = {
           @Index(name = "idx_cm_community_id", columnList = "community_id"),
           @Index(name = "idx_cm_user_id", columnList = "user_id"),
           @Index(name = "idx_cm_role", columnList = "role"),
           @Index(name = "idx_cm_joined_at", columnList = "joined_at")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = {"community_id", "user_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"community", "user"})
public class CommunityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_id", nullable = false, insertable = false, updatable = false)
    private Long communityId;

    /**
     * 소속 커뮤니티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    /**
     * 멤버 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommunityRole role = CommunityRole.MEMBER;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        if (role == null) role = CommunityRole.MEMBER;
    }
}

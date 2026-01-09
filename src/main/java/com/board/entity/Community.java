package com.board.entity;

import com.board.enums.CommunityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 커뮤니티 엔티티
 */
@Entity
@Table(name = "communities",
       indexes = {
           @Index(name = "idx_community_type", columnList = "type"),
           @Index(name = "idx_community_is_active", columnList = "is_active"),
           @Index(name = "idx_community_created_at", columnList = "created_at"),
           @Index(name = "idx_community_member_count", columnList = "member_count")
       })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"owner", "members", "categories"})
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_id", nullable = false, insertable = false, updatable = false)
    private Long ownerId;

    /**
     * 커뮤니티 소유자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommunityType type = CommunityType.PUBLIC;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "member_count", nullable = false)
    private Integer memberCount = 0;

    @Column(name = "board_count", nullable = false)
    private Integer boardCount = 0;

    @Column(length = 500)
    private String rules;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "banner_image_url", length = 500)
    private String bannerImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 커뮤니티 멤버 목록 (Cascade ALL + orphanRemoval)
     */
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityMember> members = new ArrayList<>();

    /**
     * 커뮤니티 카테고리 목록 (Cascade ALL + orphanRemoval)
     */
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityCategory> categories = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (memberCount == null) memberCount = 0;
        if (boardCount == null) boardCount = 0;
        if (isActive == null) isActive = true;
        if (type == null) type = CommunityType.PUBLIC;
    }

    // 비즈니스 로직 메서드
    public void increaseMemberCount() {
        this.memberCount++;
    }

    public void decreaseMemberCount() {
        if (this.memberCount > 0) {
            this.memberCount--;
        }
    }

    public void increaseBoardCount() {
        this.boardCount++;
    }

    public void decreaseBoardCount() {
        if (this.boardCount > 0) {
            this.boardCount--;
        }
    }
}

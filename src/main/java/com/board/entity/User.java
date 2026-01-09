package com.board.entity;

import com.board.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"boards", "comments", "likes"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.MEMBER;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image", length = 500)
    private String profileImage;

    /**
     * 탈퇴 시간
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 탈퇴 사유
     */
    @Column(name = "delete_reason", length = 500)
    private String deleteReason;

    /**
     * 사용자가 작성한 게시글 목록
     * IMPORTANT: cascade 제거 - 사용자 삭제 시 게시글은 유지되어야 함
     * 게시글은 "탈퇴한 사용자"로 표시됨
     */
    @OneToMany(mappedBy = "user")
    private List<Board> boards = new ArrayList<>();

    /**
     * 사용자가 작성한 댓글 목록
     * IMPORTANT: cascade 제거 - 사용자 삭제 시 댓글은 유지되어야 함
     * 댓글은 "탈퇴한 사용자"로 표시됨
     */
    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    /**
     * 사용자가 누른 좋아요 목록
     * IMPORTANT: cascade 제거 - 사용자 삭제 시 좋아요는 유지되어야 함
     */
    @OneToMany(mappedBy = "user")
    private List<Like> likes = new ArrayList<>();

    // 비즈니스 로직 메서드
    @PrePersist
    protected void onCreate() {
        if (role == null) role = UserRole.MEMBER;
        if (enabled == false) enabled = true;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void markAsDeleted(String reason) {
        this.deletedAt = LocalDateTime.now();
        this.deleteReason = reason;
    }
}

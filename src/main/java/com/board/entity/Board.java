package com.board.entity;

import com.board.enums.BoardStatus;
import com.board.enums.ReactionType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"comments", "shares", "bookmarks", "user"})
@Table(name = "board",
       indexes = {
           @Index(name = "idx_board_status", columnList = "status"),
           @Index(name = "idx_board_category", columnList = "category_id"),
           @Index(name = "idx_board_pinned", columnList = "is_pinned, pinned_until")
       })
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String author;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    /**
     * 게시글 작성자 (User 엔티티와의 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 50)
    private String nickname;

    @Column(name = "category_id")
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoardStatus status = BoardStatus.PUBLIC;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "is_important", nullable = false)
    private Boolean isImportant = false;

    @Column(name = "pinned_until")
    private LocalDateTime pinnedUntil;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reaction_counts", columnDefinition = "jsonb")
    private String reactionCounts = "{\"LIKE\":0,\"LOVE\":0,\"HAHA\":0,\"WOW\":0,\"SAD\":0,\"ANGRY\":0}";

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "is_draft", nullable = false)
    private Boolean isDraft = false;

    @Column(name = "community_id")
    private Long communityId;

    /**
     * 게시글이 속한 커뮤니티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", insertable = false, updatable = false)
    private Community community;

    @Column(name = "community_category_id")
    private Long communityCategoryId;

    /**
     * 게시글이 속한 커뮤니티 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_category_id", insertable = false, updatable = false)
    private CommunityCategory communityCategory;

    /**
     * 게시글의 댓글 목록
     */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    /**
     * 게시글의 공유 목록
     */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Share> shares = new ArrayList<>();

    /**
     * 게시글의 북마크 목록
     */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    // Helper method for reaction counts
    @Transient
    public Map<String, Integer> getReactionCountsMap() {
        try {
            return new ObjectMapper().readValue(reactionCounts,
                    new TypeReference<Map<String, Integer>>(){});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Transient
    private ReactionType userReaction;  // 뷰에서 사용자 반응 표시용

    // 비즈니스 로직 메서드
    @PrePersist
    protected void onCreate() {
        if (viewCount == null) viewCount = 0;
        if (likeCount == null) likeCount = 0;
        if (commentCount == null) commentCount = 0;
        if (isPinned == null) isPinned = false;
        if (isImportant == null) isImportant = false;
        if (isDraft == null) isDraft = false;
        if (status == null) status = BoardStatus.PUBLIC;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}

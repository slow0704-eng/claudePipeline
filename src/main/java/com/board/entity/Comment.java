package com.board.entity;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"board", "user", "replies"})
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id", insertable = false, updatable = false)
    private Long boardId;

    /**
     * 댓글이 속한 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    /**
     * 댓글 작성자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reaction_counts", columnDefinition = "jsonb")
    private String reactionCounts = "{\"LIKE\":0,\"HELPFUL\":0,\"FUNNY\":0,\"WOW\":0,\"SAD\":0,\"ANGRY\":0,\"THINKING\":0,\"CELEBRATE\":0}";

    // Transient field for child comments (not persisted to DB)
    @Transient
    private List<Comment> replies = new ArrayList<>();

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
        if (likeCount == null) likeCount = 0;
        if (isDeleted == null) isDeleted = false;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.content = "삭제된 댓글입니다.";
    }

    public boolean isReply() {
        return parentCommentId != null;
    }
}

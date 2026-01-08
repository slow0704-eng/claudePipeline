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
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Transient
    private ReactionType userReaction;  // 뷰에서 사용자 반응 표시용

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getReactionCounts() {
        return reactionCounts;
    }

    public void setReactionCounts(String reactionCounts) {
        this.reactionCounts = reactionCounts;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public ReactionType getUserReaction() {
        return userReaction;
    }

    public void setUserReaction(ReactionType userReaction) {
        this.userReaction = userReaction;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", boardId=" + boardId +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", parentCommentId=" + parentCommentId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                ", likeCount=" + likeCount +
                '}';
    }
}

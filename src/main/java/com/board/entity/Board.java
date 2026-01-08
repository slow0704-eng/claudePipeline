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
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private String reactionCounts = "{\"LIKE\":0,\"HELPFUL\":0,\"FUNNY\":0,\"WOW\":0,\"SAD\":0,\"ANGRY\":0,\"THINKING\":0,\"CELEBRATE\":0}";

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "is_draft", nullable = false)
    private Boolean isDraft = false;

    /**
     * 게시글의 댓글 목록
     */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    /**
     * 게시글의 좋아요 목록
     */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

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

    @Transient
    private ReactionType userReaction;  // 뷰에서 사용자 반응 표시용

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BoardStatus getStatus() {
        return status;
    }

    public void setStatus(BoardStatus status) {
        this.status = status;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }

    public Boolean getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(Boolean isImportant) {
        this.isImportant = isImportant;
    }

    public LocalDateTime getPinnedUntil() {
        return pinnedUntil;
    }

    public void setPinnedUntil(LocalDateTime pinnedUntil) {
        this.pinnedUntil = pinnedUntil;
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

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
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

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Share> getShares() {
        return shares;
    }

    public void setShares(List<Share> shares) {
        this.shares = shares;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", categoryId=" + categoryId +
                ", status=" + status +
                ", isPinned=" + isPinned +
                ", isImportant=" + isImportant +
                ", pinnedUntil=" + pinnedUntil +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", isDraft=" + isDraft +
                '}';
    }
}

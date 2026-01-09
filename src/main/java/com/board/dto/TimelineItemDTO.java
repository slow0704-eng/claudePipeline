package com.board.dto;

import java.time.LocalDateTime;

/**
 * 타임라인 아이템 DTO
 * 사용자의 타임라인에 표시되는 게시글 또는 활동 정보
 */
public class TimelineItemDTO {

    private Long id;
    private String type; // POST, COMMENT, LIKE, FOLLOW 등
    private String title;
    private String content;
    private Long userId;
    private String username;
    private String userProfileImage;
    private LocalDateTime createdAt;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLiked;
    private Boolean isBookmarked;

    public TimelineItemDTO() {
    }

    public TimelineItemDTO(Long id, String type, String title, String content, Long userId,
                          String username, String userProfileImage, LocalDateTime createdAt,
                          Integer likeCount, Integer commentCount, Boolean isLiked, Boolean isBookmarked) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.userProfileImage = userProfileImage;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
        this.isBookmarked = isBookmarked;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Boolean getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(Boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
}

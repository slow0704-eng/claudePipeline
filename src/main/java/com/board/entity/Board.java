package com.board.entity;

import com.board.enums.BoardStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
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

    @Column(name = "user_id")
    private Long userId;

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

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "is_draft", nullable = false)
    private Boolean isDraft = false;
}

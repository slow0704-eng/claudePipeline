package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_topic_follow",
       indexes = {
           @Index(name = "idx_utf_user_id", columnList = "user_id"),
           @Index(name = "idx_utf_topic_id", columnList = "topic_id"),
           @Index(name = "idx_utf_followed_at", columnList = "followed_at")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id"}))
@Data
public class UserTopicFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @CreationTimestamp
    @Column(name = "followed_at", updatable = false)
    private LocalDateTime followedAt;
}

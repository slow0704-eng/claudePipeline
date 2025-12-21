package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows",
       uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
@Data
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "follower_id")
    private Long followerId;

    @Column(nullable = false, name = "following_id")
    private Long followingId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}

package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history",
       indexes = @Index(name = "idx_user_keyword", columnList = "user_id, searched_at"))
@Data
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 200)
    private String keyword;

    @CreationTimestamp
    @Column(updatable = false, name = "searched_at")
    private LocalDateTime searchedAt;
}

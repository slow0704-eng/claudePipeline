package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_topic",
       indexes = {
           @Index(name = "idx_bt_board_id", columnList = "board_id"),
           @Index(name = "idx_bt_topic_id", columnList = "topic_id")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "topic_id"}))
@Data
public class BoardTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

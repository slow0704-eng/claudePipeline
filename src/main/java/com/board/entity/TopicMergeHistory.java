package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "topic_merge_history",
       indexes = {
           @Index(name = "idx_tmh_source", columnList = "source_topic_id"),
           @Index(name = "idx_tmh_target", columnList = "target_topic_id"),
           @Index(name = "idx_tmh_merged_at", columnList = "merged_at")
       })
@Data
public class TopicMergeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_topic_id", nullable = false)
    private Long sourceTopicId;

    @Column(name = "source_topic_name", nullable = false, length = 100)
    private String sourceTopicName;

    @Column(name = "target_topic_id", nullable = false)
    private Long targetTopicId;

    @Column(name = "target_topic_name", nullable = false, length = 100)
    private String targetTopicName;

    @Column(name = "merged_by_user_id", nullable = false)
    private Long mergedByUserId;

    @Column(name = "boards_affected", nullable = false)
    private Integer boardsAffected = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "merged_at", updatable = false)
    private LocalDateTime mergedAt;
}

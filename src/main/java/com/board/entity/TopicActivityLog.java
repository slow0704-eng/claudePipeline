package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 토픽 활동 로그 엔티티
 * - 사용자의 토픽 관련 활동 기록
 * - 추천 알고리즘에 사용
 */
@Entity
@Table(name = "topic_activity_log",
       indexes = {
           @Index(name = "idx_tal_user_id", columnList = "user_id"),
           @Index(name = "idx_tal_topic_id", columnList = "topic_id"),
           @Index(name = "idx_tal_created_at", columnList = "created_at")
       })
@Data
public class TopicActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 토픽 ID
     */
    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    /**
     * 게시글 ID
     */
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    /**
     * 활동 타입: VIEW (조회), CREATE (작성), LIKE (좋아요)
     */
    @Column(name = "activity_type", nullable = false, length = 20)
    private String activityType;

    /**
     * 활동 점수 (가중치)
     * - VIEW: 1.0
     * - CREATE: 5.0
     * - LIKE: 3.0
     */
    @Column(name = "activity_score", precision = 5, scale = 2)
    private BigDecimal activityScore;

    /**
     * 활동 일시
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 활동 타입 상수
     */
    public static final String ACTIVITY_TYPE_VIEW = "VIEW";
    public static final String ACTIVITY_TYPE_CREATE = "CREATE";
    public static final String ACTIVITY_TYPE_LIKE = "LIKE";

    /**
     * 활동 점수 상수
     */
    public static final BigDecimal SCORE_VIEW = new BigDecimal("1.0");
    public static final BigDecimal SCORE_CREATE = new BigDecimal("5.0");
    public static final BigDecimal SCORE_LIKE = new BigDecimal("3.0");
}

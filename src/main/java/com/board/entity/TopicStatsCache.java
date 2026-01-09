package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 토픽 통계 캐시 엔티티
 * - 토픽 관련 통계를 캐싱하여 성능 최적화
 * - 주기적으로 갱신 (스케줄러)
 */
@Entity
@Table(name = "topic_stats_cache",
       indexes = {
           @Index(name = "idx_tsc_popularity_rank", columnList = "popularity_rank"),
           @Index(name = "idx_tsc_trending_rank", columnList = "trending_rank"),
           @Index(name = "idx_tsc_growth_rank", columnList = "growth_rank"),
           @Index(name = "idx_tsc_follower_rank", columnList = "follower_rank")
       })
@Data
public class TopicStatsCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 토픽 ID (유니크)
     */
    @Column(name = "topic_id", nullable = false, unique = true)
    private Long topicId;

    /**
     * 전체 게시글 수
     */
    @Column(name = "post_count", nullable = false)
    private Long postCount = 0L;

    /**
     * 팔로워 수
     */
    @Column(name = "follower_count", nullable = false)
    private Long followerCount = 0L;

    /**
     * 최근 7일 게시글 수
     */
    @Column(name = "posts_7days", nullable = false)
    private Long posts7days = 0L;

    /**
     * 최근 30일 게시글 수
     */
    @Column(name = "posts_30days", nullable = false)
    private Long posts30days = 0L;

    /**
     * 최근 7일 성장률 (%)
     * (최근 7일 게시글 수 / 이전 7일 게시글 수 - 1) * 100
     */
    @Column(name = "growth_rate_7days", precision = 10, scale = 4)
    private BigDecimal growthRate7days = BigDecimal.ZERO;

    /**
     * 인기도 순위 (usageCount 기준)
     */
    @Column(name = "popularity_rank")
    private Integer popularityRank;

    /**
     * 트렌딩 순위 (최근 7일 게시글 수 기준)
     */
    @Column(name = "trending_rank")
    private Integer trendingRank;

    /**
     * 성장률 순위 (growth_rate_7days 기준)
     */
    @Column(name = "growth_rank")
    private Integer growthRank;

    /**
     * 팔로워 순위 (follower_count 기준)
     */
    @Column(name = "follower_rank")
    private Integer followerRank;

    /**
     * 마지막 갱신 시간
     */
    @UpdateTimestamp
    @Column(name = "last_refreshed_at")
    private LocalDateTime lastRefreshedAt;
}

package com.board.repository;

import com.board.entity.TopicStatsCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicStatsCacheRepository extends JpaRepository<TopicStatsCache, Long> {

    /**
     * 토픽 ID로 캐시 조회
     */
    Optional<TopicStatsCache> findByTopicId(Long topicId);

    /**
     * 토픽 ID 존재 여부 확인
     */
    boolean existsByTopicId(Long topicId);

    /**
     * 인기도 순위 기준 조회 (상위 N개)
     */
    @Query("SELECT sc FROM TopicStatsCache sc " +
           "WHERE sc.popularityRank IS NOT NULL " +
           "ORDER BY sc.popularityRank ASC")
    List<TopicStatsCache> findTopByPopularityRank(org.springframework.data.domain.Pageable pageable);

    /**
     * 트렌딩 순위 기준 조회 (상위 N개)
     */
    @Query("SELECT sc FROM TopicStatsCache sc " +
           "WHERE sc.trendingRank IS NOT NULL " +
           "ORDER BY sc.trendingRank ASC")
    List<TopicStatsCache> findTopByTrendingRank(org.springframework.data.domain.Pageable pageable);

    /**
     * 성장률 순위 기준 조회 (상위 N개)
     */
    @Query("SELECT sc FROM TopicStatsCache sc " +
           "WHERE sc.growthRank IS NOT NULL " +
           "ORDER BY sc.growthRank ASC")
    List<TopicStatsCache> findTopByGrowthRank(org.springframework.data.domain.Pageable pageable);

    /**
     * 팔로워 순위 기준 조회 (상위 N개)
     */
    @Query("SELECT sc FROM TopicStatsCache sc " +
           "WHERE sc.followerRank IS NOT NULL " +
           "ORDER BY sc.followerRank ASC")
    List<TopicStatsCache> findTopByFollowerRank(org.springframework.data.domain.Pageable pageable);

    /**
     * 특정 토픽 ID 목록의 캐시 조회
     */
    @Query("SELECT sc FROM TopicStatsCache sc WHERE sc.topicId IN :topicIds")
    List<TopicStatsCache> findByTopicIdIn(@Param("topicIds") List<Long> topicIds);

    /**
     * 모든 캐시 삭제
     */
    @Query("DELETE FROM TopicStatsCache")
    void deleteAllCache();
}

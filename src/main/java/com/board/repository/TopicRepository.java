package com.board.repository;

import com.board.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    // 기본 쿼리
    Optional<Topic> findByName(String name);
    boolean existsByName(String name);

    // 계층 구조 쿼리 (Menu 패턴)
    List<Topic> findByParentIdIsNullOrderByDisplayOrderAsc();
    List<Topic> findByParentIdOrderByDisplayOrderAsc(Long parentId);
    List<Topic> findByLevelOrderByDisplayOrderAsc(Integer level);

    @Query("SELECT t FROM Topic t ORDER BY t.level ASC, t.displayOrder ASC")
    List<Topic> findAllOrderByHierarchy();

    // 검색 및 자동완성
    List<Topic> findTop10ByNameContainingAndEnabledTrueOrderByUsageCountDesc(String query);

    @Query("SELECT t FROM Topic t WHERE t.enabled = true AND t.mergedIntoId IS NULL " +
           "AND LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY t.usageCount DESC")
    List<Topic> searchTopics(@Param("query") String query);

    // 인기 토픽
    @Query("SELECT t FROM Topic t WHERE t.enabled = true AND t.mergedIntoId IS NULL " +
           "ORDER BY t.usageCount DESC")
    List<Topic> findPopularTopics();

    // 활성 토픽
    @Query("SELECT t FROM Topic t WHERE t.enabled = true AND t.mergedIntoId IS NULL " +
           "ORDER BY t.usageCount DESC")
    List<Topic> findActiveTopics();

    // 병합된 토픽
    @Query("SELECT t FROM Topic t WHERE t.mergedIntoId IS NOT NULL " +
           "ORDER BY t.mergedAt DESC")
    List<Topic> findMergedTopics();

    @Query("SELECT t FROM Topic t WHERE t.mergedIntoId = :topicId")
    List<Topic> findMergedIntoTopic(@Param("topicId") Long topicId);

    // 통계
    @Query("SELECT COUNT(t), SUM(t.usageCount) FROM Topic t WHERE t.enabled = true")
    List<Object[]> getTotalStatistics();

    // 트렌딩 (최근 사용)
    @Query("SELECT t FROM Topic t WHERE t.lastUsedAt >= :since AND t.enabled = true " +
           "ORDER BY t.usageCount DESC")
    List<Topic> findTrendingTopics(@Param("since") LocalDateTime since);

    // ========== Phase 3: 토픽 탐색 쿼리 ==========

    /**
     * 새로운 토픽 조회 (생성일 기준)
     * @param since 기준 날짜 (예: 30일 전)
     * @return 새로운 토픽 목록 (최신순)
     */
    @Query("SELECT t FROM Topic t " +
           "WHERE t.enabled = true AND t.mergedIntoId IS NULL " +
           "AND t.createdAt >= :since " +
           "ORDER BY t.createdAt DESC")
    List<Topic> findNewTopics(@Param("since") LocalDateTime since);

    /**
     * 팔로워 수가 많은 토픽 조회
     * @return 토픽 ID와 팔로워 수 (Object[] = {topicId, followerCount})
     */
    @Query(value = "SELECT t.id, t.name, COUNT(utf.id) as follower_count " +
                   "FROM topic t " +
                   "LEFT JOIN user_topic_follow utf ON t.id = utf.topic_id " +
                   "WHERE t.enabled = true AND t.merged_into_id IS NULL " +
                   "GROUP BY t.id, t.name " +
                   "ORDER BY follower_count DESC",
           nativeQuery = true)
    List<Object[]> findTopicsByFollowerCount();

    /**
     * 성장세 토픽 조회 (최근 7일 vs 이전 7일 게시글 수 비교)
     * @param recentStart 최근 7일 시작 (7일 전)
     * @param oldStart 이전 7일 시작 (14일 전)
     * @return 토픽 정보와 성장 지표 (Object[] = {topicId, name, recentCount, oldCount, growthRate})
     */
    @Query(value = "SELECT t.id, t.name, t.icon, t.color, " +
                   "COUNT(CASE WHEN bt.created_at >= :recentStart THEN 1 END) as recent_count, " +
                   "COUNT(CASE WHEN bt.created_at >= :oldStart AND bt.created_at < :recentStart THEN 1 END) as old_count, " +
                   "CASE " +
                   "  WHEN COUNT(CASE WHEN bt.created_at >= :oldStart AND bt.created_at < :recentStart THEN 1 END) > 0 " +
                   "  THEN (COUNT(CASE WHEN bt.created_at >= :recentStart THEN 1 END)::float / " +
                   "        COUNT(CASE WHEN bt.created_at >= :oldStart AND bt.created_at < :recentStart THEN 1 END)::float - 1) * 100 " +
                   "  ELSE CASE WHEN COUNT(CASE WHEN bt.created_at >= :recentStart THEN 1 END) > 0 THEN 100 ELSE 0 END " +
                   "END as growth_rate " +
                   "FROM topic t " +
                   "LEFT JOIN board_topic bt ON t.id = bt.topic_id " +
                   "WHERE t.enabled = true AND t.merged_into_id IS NULL " +
                   "GROUP BY t.id, t.name, t.icon, t.color " +
                   "HAVING COUNT(CASE WHEN bt.created_at >= :recentStart THEN 1 END) > 0 " +
                   "ORDER BY growth_rate DESC, recent_count DESC " +
                   "LIMIT :limit",
           nativeQuery = true)
    List<Object[]> findGrowingTopics(
        @Param("recentStart") LocalDateTime recentStart,
        @Param("oldStart") LocalDateTime oldStart,
        @Param("limit") int limit
    );

    /**
     * 인기 토픽 조회 (Limit 적용)
     */
    @Query("SELECT t FROM Topic t WHERE t.enabled = true AND t.mergedIntoId IS NULL " +
           "ORDER BY t.usageCount DESC")
    List<Topic> findTopPopularTopics(org.springframework.data.domain.Pageable pageable);

    /**
     * 최근 활동이 활발한 토픽 (최근 7일 이내)
     */
    @Query(value = "SELECT t.id, t.name, t.icon, t.color, COUNT(bt.id) as post_count " +
                   "FROM topic t " +
                   "JOIN board_topic bt ON t.id = bt.topic_id " +
                   "WHERE t.enabled = true AND t.merged_into_id IS NULL " +
                   "AND bt.created_at >= :since " +
                   "GROUP BY t.id, t.name, t.icon, t.color " +
                   "ORDER BY post_count DESC " +
                   "LIMIT :limit",
           nativeQuery = true)
    List<Object[]> findActiveTopicsInPeriod(
        @Param("since") LocalDateTime since,
        @Param("limit") int limit
    );
}

package com.board.repository;

import com.board.entity.TopicActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TopicActivityLogRepository extends JpaRepository<TopicActivityLog, Long> {

    /**
     * 사용자의 활동 조회 (최근순)
     */
    List<TopicActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 토픽의 활동 조회 (최근순)
     */
    List<TopicActivityLog> findByTopicIdOrderByCreatedAtDesc(Long topicId);

    /**
     * 사용자의 특정 기간 활동 조회
     */
    @Query("SELECT tal FROM TopicActivityLog tal " +
           "WHERE tal.userId = :userId " +
           "AND tal.createdAt >= :since " +
           "ORDER BY tal.createdAt DESC")
    List<TopicActivityLog> findByUserIdAndCreatedAtAfter(
        @Param("userId") Long userId,
        @Param("since") LocalDateTime since
    );

    /**
     * 사용자의 토픽별 활동 점수 집계
     * @return Object[] = {topicId, totalScore}
     */
    @Query("SELECT tal.topicId, SUM(tal.activityScore) " +
           "FROM TopicActivityLog tal " +
           "WHERE tal.userId = :userId " +
           "AND tal.createdAt >= :since " +
           "GROUP BY tal.topicId " +
           "ORDER BY SUM(tal.activityScore) DESC")
    List<Object[]> findUserTopicActivityScores(
        @Param("userId") Long userId,
        @Param("since") LocalDateTime since
    );

    /**
     * 특정 토픽에서 활동한 사용자 목록 (최근순, 중복 제거)
     */
    @Query("SELECT DISTINCT tal.userId " +
           "FROM TopicActivityLog tal " +
           "WHERE tal.topicId = :topicId " +
           "AND tal.createdAt >= :since")
    List<Long> findActiveUsersByTopicId(
        @Param("topicId") Long topicId,
        @Param("since") LocalDateTime since
    );

    /**
     * 사용자가 활동한 토픽 목록 (최근순, 중복 제거)
     */
    @Query("SELECT DISTINCT tal.topicId " +
           "FROM TopicActivityLog tal " +
           "WHERE tal.userId = :userId " +
           "AND tal.createdAt >= :since")
    List<Long> findActiveTopicsByUserId(
        @Param("userId") Long userId,
        @Param("since") LocalDateTime since
    );

    /**
     * 토픽 간 co-occurrence (같이 활동한 토픽들)
     * 특정 토픽에서 활동한 사용자들이 다른 어떤 토픽에서도 활동했는지
     */
    @Query(value = "SELECT tal2.topic_id, COUNT(DISTINCT tal2.user_id) as user_count " +
                   "FROM topic_activity_log tal1 " +
                   "JOIN topic_activity_log tal2 ON tal1.user_id = tal2.user_id " +
                   "WHERE tal1.topic_id = :topicId " +
                   "AND tal2.topic_id != :topicId " +
                   "AND tal1.created_at >= :since " +
                   "AND tal2.created_at >= :since " +
                   "GROUP BY tal2.topic_id " +
                   "ORDER BY user_count DESC " +
                   "LIMIT :limit",
           nativeQuery = true)
    List<Object[]> findRelatedTopicsByCooccurrence(
        @Param("topicId") Long topicId,
        @Param("since") LocalDateTime since,
        @Param("limit") int limit
    );

    /**
     * 사용자별 토픽 활동 횟수
     */
    @Query("SELECT tal.userId, tal.topicId, COUNT(tal) " +
           "FROM TopicActivityLog tal " +
           "WHERE tal.userId IN :userIds " +
           "AND tal.createdAt >= :since " +
           "GROUP BY tal.userId, tal.topicId")
    List<Object[]> findUserTopicActivityCounts(
        @Param("userIds") List<Long> userIds,
        @Param("since") LocalDateTime since
    );

    /**
     * 중복 방지: 이미 로그가 있는지 확인
     */
    boolean existsByUserIdAndTopicIdAndBoardIdAndActivityType(
        Long userId,
        Long topicId,
        Long boardId,
        String activityType
    );

    /**
     * 오래된 로그 삭제 (데이터 정리용)
     */
    void deleteByCreatedAtBefore(LocalDateTime cutoffDate);

    /**
     * 사용자의 활동 수 조회
     */
    long countByUserId(Long userId);

    /**
     * 토픽의 활동 수 조회
     */
    long countByTopicId(Long topicId);
}

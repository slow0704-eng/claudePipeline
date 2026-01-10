package com.board.repository;

import com.board.entity.ModerationLog;
import com.board.enums.ModerationActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모더레이션 로그 Repository
 */
@Repository
public interface ModerationLogRepository extends JpaRepository<ModerationLog, Long> {

    /**
     * 커뮤니티의 모더레이션 로그 조회 (Fetch Join)
     */
    @Query(value = "SELECT ml FROM ModerationLog ml LEFT JOIN FETCH ml.moderator WHERE ml.communityId = :communityId ORDER BY ml.createdAt DESC",
           countQuery = "SELECT COUNT(ml) FROM ModerationLog ml WHERE ml.communityId = :communityId")
    Page<ModerationLog> findByCommunityIdWithModerator(@Param("communityId") Long communityId, Pageable pageable);

    /**
     * 액션 타입별 로그 조회
     */
    @Query(value = "SELECT ml FROM ModerationLog ml LEFT JOIN FETCH ml.moderator " +
           "WHERE ml.communityId = :communityId AND ml.actionType = :actionType ORDER BY ml.createdAt DESC",
           countQuery = "SELECT COUNT(ml) FROM ModerationLog ml " +
           "WHERE ml.communityId = :communityId AND ml.actionType = :actionType")
    Page<ModerationLog> findByCommunityIdAndActionType(@Param("communityId") Long communityId,
                                                         @Param("actionType") ModerationActionType actionType,
                                                         Pageable pageable);

    /**
     * 모더레이터별 로그 조회
     */
    @Query("SELECT ml FROM ModerationLog ml LEFT JOIN FETCH ml.moderator " +
           "WHERE ml.communityId = :communityId AND ml.moderatorId = :moderatorId ORDER BY ml.createdAt DESC")
    List<ModerationLog> findByCommunityIdAndModeratorId(@Param("communityId") Long communityId,
                                                          @Param("moderatorId") Long moderatorId);

    /**
     * 대상별 로그 조회
     */
    @Query("SELECT ml FROM ModerationLog ml LEFT JOIN FETCH ml.moderator " +
           "WHERE ml.communityId = :communityId AND ml.targetType = :targetType AND ml.targetId = :targetId " +
           "ORDER BY ml.createdAt DESC")
    List<ModerationLog> findByCommunityIdAndTargetTypeAndTargetId(@Param("communityId") Long communityId,
                                                                    @Param("targetType") String targetType,
                                                                    @Param("targetId") Long targetId);

    /**
     * 최근 로그 조회 (Pageable로 limit 적용)
     */
    @Query("SELECT ml FROM ModerationLog ml LEFT JOIN FETCH ml.moderator " +
           "WHERE ml.communityId = :communityId ORDER BY ml.createdAt DESC")
    List<ModerationLog> findRecentLogsByCommunity(@Param("communityId") Long communityId, Pageable pageable);

    /**
     * 기간 내 로그 수 조회
     */
    long countByCommunityIdAndCreatedAtBetween(Long communityId, LocalDateTime start, LocalDateTime end);
}

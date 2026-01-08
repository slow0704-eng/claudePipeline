package com.board.repository;

import com.board.entity.ModerationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    Page<ModerationLog> findByCommunityIdWithModeratorOrderByCreatedAtDesc(@Param("communityId") Long communityId, Pageable pageable);

    /**
     * 커뮤니티의 최근 로그 조회
     */
    @Query("SELECT ml FROM ModerationLog ml LEFT JOIN FETCH ml.moderator WHERE ml.communityId = :communityId ORDER BY ml.createdAt DESC")
    java.util.List<ModerationLog> findTop10ByCommunityIdOrderByCreatedAtDesc(@Param("communityId") Long communityId);
}

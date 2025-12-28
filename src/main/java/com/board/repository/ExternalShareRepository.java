package com.board.repository;

import com.board.entity.ExternalShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExternalShareRepository extends JpaRepository<ExternalShare, Long> {

    /**
     * 특정 게시글의 외부 공유 수
     */
    long countByBoardId(Long boardId);

    /**
     * 특정 게시글의 플랫폼별 공유 수
     */
    long countByBoardIdAndPlatform(Long boardId, ExternalShare.SharePlatform platform);

    /**
     * 특정 게시글의 외부 공유 목록 (최근순)
     */
    List<ExternalShare> findByBoardIdOrderBySharedAtDesc(Long boardId);

    /**
     * 특정 플랫폼의 공유 목록
     */
    List<ExternalShare> findByPlatformOrderBySharedAtDesc(ExternalShare.SharePlatform platform);

    /**
     * 플랫폼별 공유 통계 (게시글별)
     */
    @Query("SELECT es.boardId, es.platform, COUNT(es) as shareCount " +
           "FROM ExternalShare es " +
           "GROUP BY es.boardId, es.platform " +
           "ORDER BY shareCount DESC")
    List<Object[]> findShareStatsByPlatform();

    /**
     * 가장 많이 공유된 게시글 순위 (전체 외부 공유)
     */
    @Query("SELECT es.boardId, COUNT(es) as totalShares " +
           "FROM ExternalShare es " +
           "GROUP BY es.boardId " +
           "ORDER BY totalShares DESC")
    List<Object[]> findMostSharedBoards();

    /**
     * 가장 많이 공유된 게시글 순위 (특정 플랫폼)
     */
    @Query("SELECT es.boardId, COUNT(es) as shareCount " +
           "FROM ExternalShare es " +
           "WHERE es.platform = :platform " +
           "GROUP BY es.boardId " +
           "ORDER BY shareCount DESC")
    List<Object[]> findMostSharedBoardsByPlatform(@Param("platform") ExternalShare.SharePlatform platform);

    /**
     * 특정 기간 동안의 공유 통계
     */
    @Query("SELECT es.platform, COUNT(es) as shareCount " +
           "FROM ExternalShare es " +
           "WHERE es.sharedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY es.platform " +
           "ORDER BY shareCount DESC")
    List<Object[]> findShareStatsByPeriod(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 게시글의 플랫폼별 공유 통계
     */
    @Query("SELECT es.platform, COUNT(es) as shareCount " +
           "FROM ExternalShare es " +
           "WHERE es.boardId = :boardId " +
           "GROUP BY es.platform " +
           "ORDER BY shareCount DESC")
    List<Object[]> findPlatformStatsByBoardId(@Param("boardId") Long boardId);

    /**
     * 최근 공유 목록 (전체)
     */
    List<ExternalShare> findTop10ByOrderBySharedAtDesc();

    /**
     * 특정 사용자의 외부 공유 목록
     */
    List<ExternalShare> findByUserIdOrderBySharedAtDesc(Long userId);
}

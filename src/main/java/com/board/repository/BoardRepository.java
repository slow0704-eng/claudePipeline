package com.board.repository;

import com.board.entity.Board;
import com.board.enums.BoardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // ========== Fetch Join 쿼리 (N+1 문제 해결) ==========

    /**
     * 게시글 ID로 조회 + User Fetch Join
     * - 게시글 상세 조회 시 작성자 정보가 필요한 경우 사용
     * - N+1 문제 방지
     */
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.user WHERE b.id = :id")
    Optional<Board> findByIdWithUser(@Param("id") Long id);

    /**
     * 게시글 목록 조회 + User Fetch Join
     * - 게시글 목록에서 작성자 정보가 필요한 경우 사용
     */
    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.user WHERE b.isDraft = false ORDER BY b.createdAt DESC")
    List<Board> findAllPublishedWithUser();

    /**
     * 사용자별 게시글 조회 + User Fetch Join
     */
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.user WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    List<Board> findByUserIdWithUser(@Param("userId") Long userId);

    // ========== 기존 쿼리 메서드 ==========

    List<Board> findAllByOrderByCreatedAtDescTitleAsc();
    List<Board> findByUserIdOrderByCreatedAtDescTitleAsc(Long userId);
    List<Board> findTop10ByOrderByCreatedAtDescTitleAsc();

    // 임시저장 제외한 게시글 조회 (페이징) + User Fetch Join
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user WHERE b.isDraft = false",
           countQuery = "SELECT COUNT(b) FROM Board b WHERE b.isDraft = false")
    Page<Board> findByIsDraftFalse(Pageable pageable);

    // 통합 검색 (제목, 내용, 작성자 닉네임) - 임시저장 제외
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user " +
           "WHERE b.isDraft = false AND (" +
           "b.title LIKE %:keyword% " +
           "OR b.content LIKE %:keyword% " +
           "OR b.nickname LIKE %:keyword%)",
           countQuery = "SELECT COUNT(b) FROM Board b " +
           "WHERE b.isDraft = false AND (" +
           "b.title LIKE %:keyword% " +
           "OR b.content LIKE %:keyword% " +
           "OR b.nickname LIKE %:keyword%)")
    Page<Board> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 제목으로만 검색 - 임시저장 제외 + User Fetch Join
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user " +
           "WHERE b.title LIKE %:keyword% AND b.isDraft = false",
           countQuery = "SELECT COUNT(b) FROM Board b WHERE b.title LIKE %:keyword% AND b.isDraft = false")
    Page<Board> findByTitleContainingAndIsDraftFalse(@Param("keyword") String keyword, Pageable pageable);

    // 내용으로만 검색 - 임시저장 제외 + User Fetch Join
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user " +
           "WHERE b.content LIKE %:keyword% AND b.isDraft = false",
           countQuery = "SELECT COUNT(b) FROM Board b WHERE b.content LIKE %:keyword% AND b.isDraft = false")
    Page<Board> findByContentContainingAndIsDraftFalse(@Param("keyword") String keyword, Pageable pageable);

    // 작성자 닉네임으로 검색 - 임시저장 제외 + User Fetch Join
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user " +
           "WHERE b.nickname LIKE %:keyword% AND b.isDraft = false",
           countQuery = "SELECT COUNT(b) FROM Board b WHERE b.nickname LIKE %:keyword% AND b.isDraft = false")
    Page<Board> findByNicknameContainingAndIsDraftFalse(@Param("keyword") String keyword, Pageable pageable);

    // 고급 검색 (제목 + 내용, 최신순) - 임시저장 제외 + User Fetch Join
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user " +
           "WHERE b.isDraft = false AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) " +
           "ORDER BY b.createdAt DESC, b.title ASC",
           countQuery = "SELECT COUNT(b) FROM Board b " +
           "WHERE b.isDraft = false AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> searchByTitleOrContentOrderByCreatedAtDesc(@Param("keyword") String keyword, Pageable pageable);

    // 검색어 자동완성용 - 제목에서 키워드로 시작하는 게시글
    @Query("SELECT DISTINCT b.title FROM Board b " +
           "WHERE b.title LIKE CONCAT(:keyword, '%') " +
           "ORDER BY b.createdAt DESC")
    List<String> findTitleSuggestionsStartingWith(@Param("keyword") String keyword);

    // 임시저장 관련 쿼리
    // 사용자의 임시저장 게시글 조회
    List<Board> findByUserIdAndIsDraftTrueOrderByUpdatedAtDescTitleAsc(Long userId);

    // 사용자의 임시저장 게시글 개수
    long countByUserIdAndIsDraftTrue(Long userId);

    // 30일 이상 지난 임시저장 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.isDraft = true AND b.updatedAt < :cutoffDate")
    List<Board> findOldDrafts(@Param("cutoffDate") LocalDateTime cutoffDate);

    // 발행된 게시글만 조회 (임시저장 제외)
    @Query("SELECT b FROM Board b WHERE b.isDraft = false ORDER BY b.createdAt DESC, b.title ASC")
    List<Board> findAllPublishedOrderByCreatedAtDesc();

    // 조회수만 증가 (다른 필드에 영향 없음)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 좋아요 수 직접 업데이트 (다른 필드에 영향 없음)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Board b SET b.likeCount = :likeCount WHERE b.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("likeCount") int likeCount);

    // 반응 카운트 JSONB 업데이트
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE board SET reaction_counts = CAST(:reactionCounts AS jsonb) " +
                   "WHERE id = :id", nativeQuery = true)
    void updateReactionCounts(@Param("id") Long id, @Param("reactionCounts") String reactionCounts);

    // 통계용 쿼리 메서드
    long countByCreatedAtAfter(LocalDateTime dateTime);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Board> findByCreatedAtAfter(LocalDateTime dateTime);

    // 대량 관리 기능을 위한 쿼리
    // 상태별 게시글 조회
    List<Board> findByStatusOrderByCreatedAtDescTitleAsc(BoardStatus status);

    Page<Board> findByStatusOrderByCreatedAtDescTitleAsc(BoardStatus status, Pageable pageable);

    // 카테고리별 게시글 조회
    List<Board> findByCategoryIdOrderByCreatedAtDescTitleAsc(Long categoryId);

    Page<Board> findByCategoryIdOrderByCreatedAtDescTitleAsc(Long categoryId, Pageable pageable);

    // 상태와 카테고리로 게시글 조회
    Page<Board> findByStatusAndCategoryIdOrderByCreatedAtDescTitleAsc(BoardStatus status, Long categoryId, Pageable pageable);

    // 공지사항 관리
    // 고정된 게시글 조회 (고정 기간이 유효한 것만)
    @Query("SELECT b FROM Board b WHERE b.isPinned = true AND (b.pinnedUntil IS NULL OR b.pinnedUntil > :now) ORDER BY b.createdAt DESC, b.title ASC")
    List<Board> findActivePinnedBoards(@Param("now") LocalDateTime now);

    // 고정된 게시글 개수
    @Query("SELECT COUNT(b) FROM Board b WHERE b.isPinned = true AND (b.pinnedUntil IS NULL OR b.pinnedUntil > :now)")
    long countActivePinnedBoards(@Param("now") LocalDateTime now);

    // 고정 기간이 만료된 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.isPinned = true AND b.pinnedUntil IS NOT NULL AND b.pinnedUntil <= :now")
    List<Board> findExpiredPinnedBoards(@Param("now") LocalDateTime now);

    // 복합 검색 (관리자용) + User Fetch Join
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:categoryId IS NULL OR b.categoryId = :categoryId) AND " +
           "(:keyword IS NULL OR b.title LIKE %:keyword% OR b.content LIKE %:keyword% OR b.nickname LIKE %:keyword%) AND " +
           "(:startDate IS NULL OR b.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR b.createdAt <= :endDate) " +
           "ORDER BY b.isPinned DESC, b.createdAt DESC, b.title ASC",
           countQuery = "SELECT COUNT(b) FROM Board b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:categoryId IS NULL OR b.categoryId = :categoryId) AND " +
           "(:keyword IS NULL OR b.title LIKE %:keyword% OR b.content LIKE %:keyword% OR b.nickname LIKE %:keyword%) AND " +
           "(:startDate IS NULL OR b.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR b.createdAt <= :endDate)")
    Page<Board> searchBoardsWithFilters(
        @Param("status") BoardStatus status,
        @Param("categoryId") Long categoryId,
        @Param("keyword") String keyword,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    // 모든 게시글 조회 (관리자용 - 임시저장, 삭제된 것 포함) + User Fetch Join
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user ORDER BY b.isPinned DESC, b.createdAt DESC, b.title ASC",
           countQuery = "SELECT COUNT(b) FROM Board b")
    Page<Board> findAllBoardsForAdmin(Pageable pageable);

    /**
     * 여러 사용자의 게시글 목록 조회 (임시저장 제외) - 타임라인용 + User Fetch Join
     */
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user WHERE b.userId IN :userIds AND b.isDraft = false ORDER BY b.createdAt DESC",
           countQuery = "SELECT COUNT(b) FROM Board b WHERE b.userId IN :userIds AND b.isDraft = false")
    Page<Board> findByUserIdInAndIsDraftFalseOrderByCreatedAtDesc(@Param("userIds") List<Long> userIds, Pageable pageable);

    /**
     * 여러 게시글 ID로 조회 (임시저장 제외) - 토픽 피드용 + User Fetch Join
     */
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user WHERE b.id IN :boardIds AND b.isDraft = false ORDER BY b.createdAt DESC",
           countQuery = "SELECT COUNT(b) FROM Board b WHERE b.id IN :boardIds AND b.isDraft = false")
    Page<Board> findByIdInAndIsDraftFalseOrderByCreatedAtDesc(@Param("boardIds") List<Long> boardIds, Pageable pageable);

    /**
     * 댓글과 함께 게시글 조회 (상세 페이지용)
     * - 게시글, 작성자, 댓글, 댓글 작성자를 한번에 조회
     */
    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.user " +
           "LEFT JOIN FETCH b.comments c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE b.id = :id")
    Optional<Board> findByIdWithUserAndComments(@Param("id") Long id);

    // ========== 추가 메서드 (리팩토링 과정에서 필요) ==========

    /**
     * 사용자의 발행된 게시글 조회 (임시저장 제외, 작성일 내림차순)
     */
    @Query("SELECT b FROM Board b WHERE b.userId = :userId AND b.isDraft = false ORDER BY b.createdAt DESC")
    List<Board> findByUserIdAndIsDraftFalseOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 사용자의 임시저장 게시글 조회 (수정일 내림차순)
     */
    @Query("SELECT b FROM Board b WHERE b.userId = :userId AND b.isDraft = true ORDER BY b.updatedAt DESC")
    List<Board> findByUserIdAndIsDraftTrueOrderByUpdatedAtDesc(@Param("userId") Long userId);

    /**
     * 특정 날짜 이전의 임시저장 게시글 조회
     */
    @Query("SELECT b FROM Board b WHERE b.isDraft = true AND b.updatedAt < :cutoffDate")
    List<Board> findByIsDraftTrueAndUpdatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 고급 검색 (모든 필터 조건 적용)
     */
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user WHERE " +
           "(:title IS NULL OR b.title LIKE %:title%) AND " +
           "(:content IS NULL OR b.content LIKE %:content%) AND " +
           "(:categoryId IS NULL OR b.categoryId = :categoryId) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:startDate IS NULL OR b.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR b.createdAt <= :endDate) AND " +
           "(:isDraft IS NULL OR b.isDraft = :isDraft) AND " +
           "(:isPinned IS NULL OR b.isPinned = :isPinned) " +
           "ORDER BY b.createdAt DESC",
           countQuery = "SELECT COUNT(b) FROM Board b WHERE " +
           "(:title IS NULL OR b.title LIKE %:title%) AND " +
           "(:content IS NULL OR b.content LIKE %:content%) AND " +
           "(:categoryId IS NULL OR b.categoryId = :categoryId) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:startDate IS NULL OR b.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR b.createdAt <= :endDate) AND " +
           "(:isDraft IS NULL OR b.isDraft = :isDraft) AND " +
           "(:isPinned IS NULL OR b.isPinned = :isPinned)")
    Page<Board> advancedSearch(
        @Param("title") String title,
        @Param("content") String content,
        @Param("categoryId") Long categoryId,
        @Param("status") BoardStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("isDraft") Boolean isDraft,
        @Param("isPinned") Boolean isPinned,
        Pageable pageable
    );

    // ==================== 커뮤니티 관련 메서드 ====================

    /**
     * 커뮤니티 게시글 조회 (Fetch Join)
     */
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user " +
           "WHERE b.communityId = :communityId AND b.isDraft = false",
           countQuery = "SELECT COUNT(b) FROM Board b " +
           "WHERE b.communityId = :communityId AND b.isDraft = false")
    Page<Board> findByCommunityIdWithUser(@Param("communityId") Long communityId, Pageable pageable);

    /**
     * 커뮤니티 + 카테고리 게시글 조회 (Fetch Join)
     */
    @Query(value = "SELECT b FROM Board b LEFT JOIN FETCH b.user " +
           "WHERE b.communityId = :communityId AND b.communityCategoryId = :categoryId AND b.isDraft = false",
           countQuery = "SELECT COUNT(b) FROM Board b " +
           "WHERE b.communityId = :communityId AND b.communityCategoryId = :categoryId AND b.isDraft = false")
    Page<Board> findByCommunityIdAndCategoryIdWithUser(@Param("communityId") Long communityId,
                                                         @Param("categoryId") Long categoryId,
                                                         Pageable pageable);

    /**
     * 커뮤니티별 게시글 수
     */
    long countByCommunityIdAndIsDraftFalse(Long communityId);

    /**
     * 커뮤니티별 임시저장 게시글 수
     */
    long countByCommunityIdAndIsDraftTrue(Long communityId);

    /**
     * 특정 시간 이후 커뮤니티 게시글 수
     */
    long countByCommunityIdAndCreatedAtAfter(Long communityId, LocalDateTime since);

    /**
     * 특정 기간 내 커뮤니티 게시글 수
     */
    long countByCommunityIdAndCreatedAtBetween(Long communityId, LocalDateTime start, LocalDateTime end);

    /**
     * 커뮤니티 내 상위 활동 멤버 조회 (게시글 수 기준)
     * @return Object[] { userId, nickname, boardCount }
     */
    @Query("SELECT b.userId, b.nickname, COUNT(b) as boardCount " +
           "FROM Board b " +
           "WHERE b.communityId = :communityId AND b.isDraft = false " +
           "GROUP BY b.userId, b.nickname " +
           "ORDER BY boardCount DESC")
    List<Object[]> findTopActiveMembersByCommunity(@Param("communityId") Long communityId);

    // ==================== 대시보드 통계용 메서드 ====================

    /**
     * 최근 10개 게시글 조회 (작성일 내림차순)
     */
    List<Board> findTop10ByOrderByCreatedAtDesc();

    /**
     * 특정 기간 이후 조회수 내림차순 상위 10개
     */
    List<Board> findTop10ByCreatedAtAfterOrderByViewCountDesc(LocalDateTime since);

    /**
     * 총 조회수 합계
     */
    @Query("SELECT COALESCE(SUM(b.viewCount), 0) FROM Board b")
    Long sumViewCount();

    /**
     * 총 좋아요 합계
     */
    @Query("SELECT COALESCE(SUM(b.likeCount), 0) FROM Board b")
    Long sumLikeCount();

    /**
     * 평균 조회수
     */
    @Query("SELECT COALESCE(AVG(b.viewCount), 0) FROM Board b")
    Double avgViewCount();
}

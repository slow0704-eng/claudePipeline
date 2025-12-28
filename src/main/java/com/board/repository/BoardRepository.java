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

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByOrderByCreatedAtDesc();
    List<Board> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Board> findTop10ByOrderByCreatedAtDesc();

    // 임시저장 제외한 게시글 조회 (페이징)
    Page<Board> findByIsDraftFalse(Pageable pageable);

    // 통합 검색 (제목, 내용, 작성자 닉네임) - 임시저장 제외
    @Query("SELECT b FROM Board b " +
           "WHERE b.isDraft = false AND (" +
           "b.title LIKE %:keyword% " +
           "OR b.content LIKE %:keyword% " +
           "OR b.nickname LIKE %:keyword%)")
    Page<Board> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 제목으로만 검색 - 임시저장 제외
    Page<Board> findByTitleContainingAndIsDraftFalse(String keyword, Pageable pageable);

    // 내용으로만 검색 - 임시저장 제외
    Page<Board> findByContentContainingAndIsDraftFalse(String keyword, Pageable pageable);

    // 작성자 닉네임으로 검색 - 임시저장 제외
    Page<Board> findByNicknameContainingAndIsDraftFalse(String keyword, Pageable pageable);

    // 고급 검색 (제목 + 내용, 최신순) - 임시저장 제외
    @Query("SELECT b FROM Board b " +
           "WHERE b.isDraft = false AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) " +
           "ORDER BY b.createdAt DESC")
    Page<Board> searchByTitleOrContentOrderByCreatedAtDesc(@Param("keyword") String keyword, Pageable pageable);

    // 검색어 자동완성용 - 제목에서 키워드로 시작하는 게시글
    @Query("SELECT DISTINCT b.title FROM Board b " +
           "WHERE b.title LIKE CONCAT(:keyword, '%') " +
           "ORDER BY b.createdAt DESC")
    List<String> findTitleSuggestionsStartingWith(@Param("keyword") String keyword);

    // 임시저장 관련 쿼리
    // 사용자의 임시저장 게시글 조회
    List<Board> findByUserIdAndIsDraftTrueOrderByUpdatedAtDesc(Long userId);

    // 사용자의 임시저장 게시글 개수
    long countByUserIdAndIsDraftTrue(Long userId);

    // 30일 이상 지난 임시저장 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.isDraft = true AND b.updatedAt < :cutoffDate")
    List<Board> findOldDrafts(@Param("cutoffDate") LocalDateTime cutoffDate);

    // 발행된 게시글만 조회 (임시저장 제외)
    @Query("SELECT b FROM Board b WHERE b.isDraft = false ORDER BY b.createdAt DESC")
    List<Board> findAllPublishedOrderByCreatedAtDesc();

    // 조회수만 증가 (다른 필드에 영향 없음)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 좋아요 수 직접 업데이트 (다른 필드에 영향 없음)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Board b SET b.likeCount = :likeCount WHERE b.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("likeCount") int likeCount);

    // 통계용 쿼리 메서드
    long countByCreatedAtAfter(LocalDateTime dateTime);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Board> findByCreatedAtAfter(LocalDateTime dateTime);

    // 대량 관리 기능을 위한 쿼리
    // 상태별 게시글 조회
    List<Board> findByStatusOrderByCreatedAtDesc(BoardStatus status);

    Page<Board> findByStatusOrderByCreatedAtDesc(BoardStatus status, Pageable pageable);

    // 카테고리별 게시글 조회
    List<Board> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    Page<Board> findByCategoryIdOrderByCreatedAtDesc(Long categoryId, Pageable pageable);

    // 상태와 카테고리로 게시글 조회
    Page<Board> findByStatusAndCategoryIdOrderByCreatedAtDesc(BoardStatus status, Long categoryId, Pageable pageable);

    // 공지사항 관리
    // 고정된 게시글 조회 (고정 기간이 유효한 것만)
    @Query("SELECT b FROM Board b WHERE b.isPinned = true AND (b.pinnedUntil IS NULL OR b.pinnedUntil > :now) ORDER BY b.createdAt DESC")
    List<Board> findActivePinnedBoards(@Param("now") LocalDateTime now);

    // 고정된 게시글 개수
    @Query("SELECT COUNT(b) FROM Board b WHERE b.isPinned = true AND (b.pinnedUntil IS NULL OR b.pinnedUntil > :now)")
    long countActivePinnedBoards(@Param("now") LocalDateTime now);

    // 고정 기간이 만료된 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.isPinned = true AND b.pinnedUntil IS NOT NULL AND b.pinnedUntil <= :now")
    List<Board> findExpiredPinnedBoards(@Param("now") LocalDateTime now);

    // 복합 검색 (관리자용)
    @Query("SELECT b FROM Board b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:categoryId IS NULL OR b.categoryId = :categoryId) AND " +
           "(:keyword IS NULL OR b.title LIKE %:keyword% OR b.content LIKE %:keyword% OR b.nickname LIKE %:keyword%) AND " +
           "(:startDate IS NULL OR b.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR b.createdAt <= :endDate) " +
           "ORDER BY b.isPinned DESC, b.createdAt DESC")
    Page<Board> searchBoardsWithFilters(
        @Param("status") BoardStatus status,
        @Param("categoryId") Long categoryId,
        @Param("keyword") String keyword,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    // 모든 게시글 조회 (관리자용 - 임시저장, 삭제된 것 포함)
    @Query("SELECT b FROM Board b ORDER BY b.isPinned DESC, b.createdAt DESC")
    Page<Board> findAllBoardsForAdmin(Pageable pageable);
}

package com.board.repository;

import com.board.entity.Community;
import com.board.enums.CommunityType;
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

/**
 * 커뮤니티 Repository
 */
@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    /**
     * 커뮤니티 이름 중복 확인
     */
    boolean existsByName(String name);

    /**
     * ID로 커뮤니티 조회 (Fetch Join으로 N+1 방지)
     */
    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.owner WHERE c.id = :id")
    Optional<Community> findByIdWithOwner(@Param("id") Long id);

    /**
     * 전체 커뮤니티 목록 조회 (관리자용, Fetch Join)
     */
    @Query(value = "SELECT c FROM Community c LEFT JOIN FETCH c.owner",
           countQuery = "SELECT COUNT(c) FROM Community c")
    Page<Community> findAllWithOwner(Pageable pageable);

    /**
     * 활성 커뮤니티 목록 조회 (Fetch Join)
     */
    @Query(value = "SELECT c FROM Community c LEFT JOIN FETCH c.owner WHERE c.isActive = true",
           countQuery = "SELECT COUNT(c) FROM Community c WHERE c.isActive = true")
    Page<Community> findActiveCommunitiesWithOwner(Pageable pageable);

    /**
     * 타입별 커뮤니티 조회 (Fetch Join)
     */
    @Query(value = "SELECT c FROM Community c LEFT JOIN FETCH c.owner WHERE c.type = :type AND c.isActive = true",
           countQuery = "SELECT COUNT(c) FROM Community c WHERE c.type = :type AND c.isActive = true")
    Page<Community> findByTypeWithOwner(@Param("type") CommunityType type, Pageable pageable);

    /**
     * 소유자별 커뮤니티 조회 (Fetch Join)
     */
    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.owner WHERE c.ownerId = :ownerId ORDER BY c.createdAt DESC")
    List<Community> findByOwnerIdWithOwner(@Param("ownerId") Long ownerId);

    /**
     * 커뮤니티 검색 (이름, 설명)
     */
    @Query(value = "SELECT c FROM Community c LEFT JOIN FETCH c.owner " +
           "WHERE c.isActive = true AND (c.name LIKE %:keyword% OR c.description LIKE %:keyword%)",
           countQuery = "SELECT COUNT(c) FROM Community c " +
           "WHERE c.isActive = true AND (c.name LIKE %:keyword% OR c.description LIKE %:keyword%)")
    Page<Community> searchCommunitiesWithOwner(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 멤버 수 업데이트
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Community c SET c.memberCount = :memberCount WHERE c.id = :id")
    void updateMemberCount(@Param("id") Long id, @Param("memberCount") int memberCount);

    /**
     * 게시글 수 업데이트
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Community c SET c.boardCount = :boardCount WHERE c.id = :id")
    void updateBoardCount(@Param("id") Long id, @Param("boardCount") int boardCount);

    /**
     * 활성 커뮤니티 수
     */
    long countByIsActive(Boolean isActive);

    /**
     * 타입별 커뮤니티 수
     */
    long countByType(CommunityType type);

    /**
     * 최근 생성된 커뮤니티 수
     */
    long countByCreatedAtAfter(LocalDateTime since);
}

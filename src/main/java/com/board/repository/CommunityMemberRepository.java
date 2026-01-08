package com.board.repository;

import com.board.entity.CommunityMember;
import com.board.enums.CommunityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 커뮤니티 멤버 Repository
 */
@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    /**
     * 커뮤니티의 모든 멤버 조회 (Fetch Join)
     */
    @Query("SELECT cm FROM CommunityMember cm LEFT JOIN FETCH cm.user WHERE cm.communityId = :communityId")
    List<CommunityMember> findByCommunityIdWithUser(@Param("communityId") Long communityId);

    /**
     * 사용자의 모든 커뮤니티 멤버십 조회 (Fetch Join)
     */
    @Query("SELECT cm FROM CommunityMember cm LEFT JOIN FETCH cm.community WHERE cm.userId = :userId")
    List<CommunityMember> findByUserIdWithCommunity(@Param("userId") Long userId);

    /**
     * 멤버십 존재 여부 확인
     */
    boolean existsByCommunityIdAndUserId(Long communityId, Long userId);

    /**
     * 멤버십 조회
     */
    Optional<CommunityMember> findByCommunityIdAndUserId(Long communityId, Long userId);

    /**
     * 역할별 멤버 조회 (Fetch Join)
     */
    @Query("SELECT cm FROM CommunityMember cm LEFT JOIN FETCH cm.user WHERE cm.communityId = :communityId AND cm.role = :role")
    List<CommunityMember> findByCommunityIdAndRoleWithUser(@Param("communityId") Long communityId,
                                                             @Param("role") CommunityRole role);

    /**
     * 커뮤니티별 멤버 수
     */
    long countByCommunityId(Long communityId);

    /**
     * 특정 시간 이후 가입한 멤버 수
     */
    @Query("SELECT COUNT(cm) FROM CommunityMember cm WHERE cm.communityId = :communityId AND cm.joinedAt >= :since")
    long countByJoinedAtAfter(@Param("communityId") Long communityId, @Param("since") LocalDateTime since);

    /**
     * 멤버 삭제
     */
    void deleteByCommunityIdAndUserId(Long communityId, Long userId);
}

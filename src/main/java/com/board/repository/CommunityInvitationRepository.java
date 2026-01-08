package com.board.repository;

import com.board.entity.CommunityInvitation;
import com.board.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 커뮤니티 초대 Repository
 */
@Repository
public interface CommunityInvitationRepository extends JpaRepository<CommunityInvitation, Long> {

    /**
     * 대기중인 초대 조회
     */
    @Query("SELECT ci FROM CommunityInvitation ci WHERE ci.communityId = :communityId AND ci.invitedUserId = :invitedUserId AND ci.status = 'PENDING'")
    Optional<CommunityInvitation> findPendingInvitation(@Param("communityId") Long communityId,
                                                          @Param("invitedUserId") Long invitedUserId);

    /**
     * 사용자의 대기중인 초대 목록 (Fetch Join)
     */
    @Query("SELECT ci FROM CommunityInvitation ci LEFT JOIN FETCH ci.community LEFT JOIN FETCH ci.invitedBy " +
           "WHERE ci.invitedUserId = :userId AND ci.status = 'PENDING' ORDER BY ci.createdAt DESC")
    List<CommunityInvitation> findPendingInvitationsByUserId(@Param("userId") Long userId);

    /**
     * 커뮤니티의 모든 초대 조회
     */
    @Query("SELECT ci FROM CommunityInvitation ci LEFT JOIN FETCH ci.invitedUser LEFT JOIN FETCH ci.invitedBy " +
           "WHERE ci.communityId = :communityId ORDER BY ci.createdAt DESC")
    List<CommunityInvitation> findByCommunityIdWithUsers(@Param("communityId") Long communityId);

    /**
     * 상태별 초대 수
     */
    long countByCommunityIdAndStatus(Long communityId, InvitationStatus status);
}

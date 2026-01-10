package com.board.service;

import com.board.entity.Community;
import com.board.entity.CommunityInvitation;
import com.board.entity.CommunityMember;
import com.board.entity.User;
import com.board.enums.CommunityRole;
import com.board.enums.CommunityType;
import com.board.enums.InvitationStatus;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.CommunityInvitationRepository;
import com.board.repository.CommunityMemberRepository;
import com.board.repository.CommunityRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 커뮤니티 초대 서비스
 * PRIVATE/SECRET 커뮤니티 초대 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityInvitationService {

    private final CommunityInvitationRepository invitationRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserService userService;
    private final CommunityMemberService communityMemberService;

    /**
     * 초대 생성
     * OWNER/ADMIN만 초대 가능
     */
    @Transactional
    public CommunityInvitation createInvitation(Long communityId, Long invitedUserId, String message) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 커뮤니티 존재 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        // 초대 권한 확인 (OWNER 또는 ADMIN만)
        CommunityMember currentMember = communityMemberRepository.findByCommunityIdAndUserId(communityId, currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED));

        if (currentMember.getRole() != CommunityRole.OWNER && currentMember.getRole() != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 초대받을 사용자 확인
        User invitedUser = userService.findById(invitedUserId);

        // 이미 멤버인 경우
        if (communityMemberRepository.existsByCommunityIdAndUserId(communityId, invitedUserId)) {
            throw new BusinessException(ErrorCode.ALREADY_MEMBER);
        }

        // 이미 대기중인 초대가 있는 경우
        if (invitationRepository.findPendingInvitation(communityId, invitedUserId).isPresent()) {
            throw new BusinessException(ErrorCode.INVITATION_ALREADY_SENT);
        }

        // 초대 생성
        CommunityInvitation invitation = new CommunityInvitation();
        invitation.setCommunity(community);
        invitation.setCommunityId(communityId);
        invitation.setInvitedBy(currentUser);
        invitation.setInvitedById(currentUser.getId());
        invitation.setInvitedUser(invitedUser);
        invitation.setInvitedUserId(invitedUserId);
        invitation.setMessage(message);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        CommunityInvitation saved = invitationRepository.save(invitation);

        log.info("커뮤니티 초대 생성: invitationId={}, communityId={}, invitedUserId={}, invitedBy={}",
                saved.getId(), communityId, invitedUserId, currentUser.getId());

        return saved;
    }

    /**
     * 초대 수락
     */
    @Transactional
    public void acceptInvitation(Long invitationId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 초대 조회
        CommunityInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

        // 초대받은 사용자만 수락 가능
        if (!invitation.getInvitedUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 이미 처리된 초대
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVITATION_ALREADY_PROCESSED);
        }

        // 만료된 초대
        if (invitation.isExpired()) {
            throw new BusinessException(ErrorCode.INVITATION_EXPIRED);
        }

        // 초대 수락 처리
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);

        // 멤버십 생성
        CommunityMember member = CommunityMember.builder().build();
        member.setCommunity(invitation.getCommunity());
        member.setCommunityId(invitation.getCommunityId());
        member.setUser(currentUser);
        member.setUserId(currentUser.getId());
        member.setRole(CommunityRole.MEMBER);

        communityMemberRepository.save(member);

        // 커뮤니티 멤버 수 증가
        Community community = communityRepository.findById(invitation.getCommunityId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));
        community.increaseMemberCount();
        communityRepository.save(community);

        log.info("커뮤니티 초대 수락: invitationId={}, communityId={}, userId={}",
                invitationId, invitation.getCommunityId(), currentUser.getId());
    }

    /**
     * 초대 거절
     */
    @Transactional
    public void rejectInvitation(Long invitationId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 초대 조회
        CommunityInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

        // 초대받은 사용자만 거절 가능
        if (!invitation.getInvitedUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 이미 처리된 초대
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVITATION_ALREADY_PROCESSED);
        }

        // 초대 거절 처리
        invitation.setStatus(InvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);

        log.info("커뮤니티 초대 거절: invitationId={}, communityId={}, userId={}",
                invitationId, invitation.getCommunityId(), currentUser.getId());
    }

    /**
     * 초대 취소 (초대한 사람만 가능)
     */
    @Transactional
    public void cancelInvitation(Long invitationId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 초대 조회
        CommunityInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

        // 초대한 사람 또는 ADMIN만 취소 가능
        boolean canCancel = invitation.getInvitedById().equals(currentUser.getId()) ||
                communityMemberService.getMemberRole(invitation.getCommunityId(), currentUser.getId()) == CommunityRole.OWNER ||
                communityMemberService.getMemberRole(invitation.getCommunityId(), currentUser.getId()) == CommunityRole.ADMIN;

        if (!canCancel) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 이미 처리된 초대는 취소 불가
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVITATION_ALREADY_PROCESSED);
        }

        // 초대 취소 (삭제)
        invitationRepository.delete(invitation);

        log.info("커뮤니티 초대 취소: invitationId={}, communityId={}, canceledBy={}",
                invitationId, invitation.getCommunityId(), currentUser.getId());
    }

    /**
     * 사용자가 받은 대기중인 초대 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityInvitation> getPendingInvitations(Long userId) {
        return invitationRepository.findPendingInvitationsByUserId(userId);
    }

    /**
     * 현재 사용자가 받은 대기중인 초대 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityInvitation> getMyPendingInvitations() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        return invitationRepository.findPendingInvitationsByUserId(currentUser.getId());
    }

    /**
     * 커뮤니티의 모든 초대 조회 (OWNER/ADMIN만)
     */
    @Transactional(readOnly = true)
    public List<CommunityInvitation> getCommunityInvitations(Long communityId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인
        CommunityRole role = communityMemberService.getMemberRole(communityId, currentUser.getId());
        if (role != CommunityRole.OWNER && role != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return invitationRepository.findByCommunityIdWithUsers(communityId);
    }

    /**
     * 초대 단건 조회
     */
    @Transactional(readOnly = true)
    public CommunityInvitation getInvitationById(Long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));
    }

    /**
     * 만료된 초대 정리 (스케줄러용)
     */
    @Transactional
    public void cleanupExpiredInvitations() {
        // 구현 필요 시 추가
        log.debug("만료된 초대 정리 작업 실행");
    }
}

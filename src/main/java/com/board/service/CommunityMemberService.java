package com.board.service;

import com.board.entity.Community;
import com.board.entity.CommunityMember;
import com.board.entity.ModerationLog;
import com.board.entity.User;
import com.board.enums.CommunityRole;
import com.board.enums.CommunityType;
import com.board.enums.ModerationActionType;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.CommunityMemberRepository;
import com.board.repository.CommunityRepository;
import com.board.repository.ModerationLogRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 커뮤니티 멤버십 서비스
 * 멤버 가입, 탈퇴, 역할 변경, 퇴출 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityMemberService {

    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityRepository communityRepository;
    private final ModerationLogRepository moderationLogRepository;
    private final UserService userService;

    /**
     * 커뮤니티 가입
     * PUBLIC 커뮤니티만 자유 가입 가능
     * PRIVATE/SECRET 커뮤니티는 초대 필요
     */
    @Transactional
    public CommunityMember joinCommunity(Long communityId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 커뮤니티 존재 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        // 이미 가입된 멤버인지 확인
        if (communityMemberRepository.existsByCommunityIdAndUserId(communityId, currentUser.getId())) {
            throw new BusinessException(ErrorCode.ALREADY_MEMBER);
        }

        // PUBLIC 커뮤니티만 자유 가입 가능
        if (community.getType() != CommunityType.PUBLIC) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 멤버십 생성
        CommunityMember member = CommunityMember.builder().build();
        member.setCommunity(community);
        member.setCommunityId(communityId);
        member.setUser(currentUser);
        member.setUserId(currentUser.getId());
        member.setRole(CommunityRole.MEMBER);

        CommunityMember saved = communityMemberRepository.save(member);

        // 멤버 수 증가
        community.increaseMemberCount();
        communityRepository.save(community);

        log.info("커뮤니티 가입 완료: communityId={}, userId={}, role={}",
                communityId, currentUser.getId(), CommunityRole.MEMBER);

        return saved;
    }

    /**
     * 커뮤니티 탈퇴
     * OWNER는 탈퇴 불가 (소유권 이전 후 탈퇴 가능)
     */
    @Transactional
    public void leaveCommunity(Long communityId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 멤버십 조회
        CommunityMember member = communityMemberRepository.findByCommunityIdAndUserId(communityId, currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // OWNER는 탈퇴 불가
        if (member.getRole() == CommunityRole.OWNER) {
            throw new BusinessException(ErrorCode.OWNER_CANNOT_LEAVE);
        }

        // 멤버십 삭제
        communityMemberRepository.delete(member);

        // 멤버 수 감소
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));
        community.decreaseMemberCount();
        communityRepository.save(community);

        log.info("커뮤니티 탈퇴 완료: communityId={}, userId={}", communityId, currentUser.getId());
    }

    /**
     * 초대를 통한 커뮤니티 가입
     * PRIVATE/SECRET 커뮤니티 가입 시 사용
     */
    @Transactional
    public CommunityMember joinByInvitation(Long communityId, Long invitedUserId, CommunityRole role) {
        // 커뮤니티 존재 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        // 이미 가입된 멤버인지 확인
        if (communityMemberRepository.existsByCommunityIdAndUserId(communityId, invitedUserId)) {
            throw new BusinessException(ErrorCode.ALREADY_MEMBER);
        }

        // 초대받은 사용자 조회 (User 엔티티는 이미 있다고 가정)
        User invitedUser = User.builder().build();
        invitedUser.setId(invitedUserId);

        // 멤버십 생성
        CommunityMember member = CommunityMember.builder().build();
        member.setCommunity(community);
        member.setCommunityId(communityId);
        member.setUser(invitedUser);
        member.setUserId(invitedUserId);
        member.setRole(role != null ? role : CommunityRole.MEMBER);

        CommunityMember saved = communityMemberRepository.save(member);

        // 멤버 수 증가
        community.increaseMemberCount();
        communityRepository.save(community);

        log.info("초대를 통한 커뮤니티 가입 완료: communityId={}, userId={}, role={}",
                communityId, invitedUserId, member.getRole());

        return saved;
    }

    /**
     * 멤버 역할 변경
     * OWNER만 가능
     * OWNER 역할은 변경 불가 (소유권 이전 기능 별도 필요)
     */
    @Transactional
    public CommunityMember changeMemberRole(Long communityId, Long targetUserId, CommunityRole newRole) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 현재 사용자가 OWNER인지 확인
        CommunityMember currentMember = communityMemberRepository.findByCommunityIdAndUserId(communityId, currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (currentMember.getRole() != CommunityRole.OWNER) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 대상 멤버 조회
        CommunityMember targetMember = communityMemberRepository.findByCommunityIdAndUserId(communityId, targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // OWNER 역할은 변경 불가
        if (targetMember.getRole() == CommunityRole.OWNER) {
            throw new BusinessException(ErrorCode.CANNOT_CHANGE_OWNER_ROLE);
        }

        // 새로운 역할이 OWNER인 경우 불가 (소유권 이전 기능 별도 필요)
        if (newRole == CommunityRole.OWNER) {
            throw new BusinessException(ErrorCode.CANNOT_CHANGE_OWNER_ROLE);
        }

        CommunityRole oldRole = targetMember.getRole();
        targetMember.setRole(newRole);
        CommunityMember saved = communityMemberRepository.save(targetMember);

        // 모더레이션 로그 기록
        logModerationAction(communityId, currentUser.getId(), ModerationActionType.MEMBER_ROLE_CHANGED,
                "MEMBER", targetUserId, String.format("역할 변경: %s -> %s", oldRole, newRole));

        log.info("멤버 역할 변경 완료: communityId={}, targetUserId={}, oldRole={}, newRole={}",
                communityId, targetUserId, oldRole, newRole);

        return saved;
    }

    /**
     * 멤버 강제 퇴출
     * OWNER 또는 ADMIN만 가능
     * OWNER는 퇴출 불가
     */
    @Transactional
    public void kickMember(Long communityId, Long targetUserId, String reason) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 현재 사용자가 ADMIN 권한이 있는지 확인
        CommunityMember currentMember = communityMemberRepository.findByCommunityIdAndUserId(communityId, currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (currentMember.getRole() != CommunityRole.OWNER && currentMember.getRole() != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 대상 멤버 조회
        CommunityMember targetMember = communityMemberRepository.findByCommunityIdAndUserId(communityId, targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // OWNER는 퇴출 불가
        if (targetMember.getRole() == CommunityRole.OWNER) {
            throw new BusinessException(ErrorCode.CANNOT_KICK_OWNER);
        }

        // ADMIN은 다른 ADMIN을 퇴출할 수 없음 (OWNER만 가능)
        if (currentMember.getRole() == CommunityRole.ADMIN && targetMember.getRole() == CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 멤버십 삭제
        communityMemberRepository.delete(targetMember);

        // 멤버 수 감소
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));
        community.decreaseMemberCount();
        communityRepository.save(community);

        // 모더레이션 로그 기록
        logModerationAction(communityId, currentUser.getId(), ModerationActionType.MEMBER_KICKED,
                "MEMBER", targetUserId, reason != null ? reason : "사유 없음");

        log.info("멤버 퇴출 완료: communityId={}, targetUserId={}, moderatorId={}, reason={}",
                communityId, targetUserId, currentUser.getId(), reason);
    }

    /**
     * 커뮤니티의 모든 멤버 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityMember> getCommunityMembers(Long communityId) {
        return communityMemberRepository.findByCommunityIdWithUser(communityId);
    }

    /**
     * 역할별 멤버 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityMember> getMembersByRole(Long communityId, CommunityRole role) {
        return communityMemberRepository.findByCommunityIdAndRoleWithUser(communityId, role);
    }

    /**
     * 사용자의 모든 커뮤니티 멤버십 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityMember> getUserMemberships(Long userId) {
        return communityMemberRepository.findByUserIdWithCommunity(userId);
    }

    /**
     * 특정 멤버 정보 조회
     */
    @Transactional(readOnly = true)
    public CommunityMember getMemberInfo(Long communityId, Long userId) {
        return communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 멤버 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isMember(Long communityId, Long userId) {
        return communityMemberRepository.existsByCommunityIdAndUserId(communityId, userId);
    }

    /**
     * 멤버 수 조회
     */
    @Transactional(readOnly = true)
    public long getMemberCount(Long communityId) {
        return communityMemberRepository.countByCommunityId(communityId);
    }

    // ==================== Private Helper Methods ====================

    /**
     * 모더레이션 로그 기록
     */
    private void logModerationAction(Long communityId, Long moderatorId,
                                      ModerationActionType actionType,
                                      String targetType, Long targetId, String reason) {
        try {
            ModerationLog log = ModerationLog.builder().build();
            log.setCommunityId(communityId);
            log.setModeratorId(moderatorId);
            log.setActionType(actionType);
            log.setTargetType(targetType);
            log.setTargetId(targetId);
            log.setReason(reason);

            moderationLogRepository.save(log);
        } catch (Exception e) {
            // 로그 기록 실패는 메인 트랜잭션에 영향을 주지 않음
            this.log.error("모더레이션 로그 기록 실패: communityId={}, actionType={}, error={}",
                    communityId, actionType, e.getMessage());
        }
    }
}

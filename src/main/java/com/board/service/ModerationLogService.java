package com.board.service;

import com.board.entity.Community;
import com.board.entity.ModerationLog;
import com.board.entity.User;
import com.board.enums.CommunityRole;
import com.board.enums.ModerationActionType;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.CommunityRepository;
import com.board.repository.ModerationLogRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모더레이션 로그 서비스
 * 커뮤니티 내 모든 모더레이션 활동 기록 및 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationLogService {

    private final ModerationLogRepository moderationLogRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberService communityMemberService;
    private final UserService userService;

    /**
     * 모더레이션 로그 생성
     */
    @Transactional
    public ModerationLog createLog(Long communityId, Long moderatorId,
                                    ModerationActionType actionType,
                                    String targetType, Long targetId,
                                    String reason, String details) {
        // 커뮤니티 존재 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        // 모더레이터 정보
        User moderator = userService.findById(moderatorId);

        // 로그 생성
        ModerationLog moderationLog = ModerationLog.builder()
                .community(community)
                .communityId(communityId)
                .moderator(moderator)
                .moderatorId(moderatorId)
                .actionType(actionType)
                .targetType(targetType)
                .targetId(targetId)
                .reason(reason)
                .details(details)
                .build();

        ModerationLog saved = moderationLogRepository.save(moderationLog);

        log.info("모더레이션 로그 생성: logId={}, communityId={}, actionType={}, targetType={}, targetId={}",
                saved.getId(), communityId, actionType, targetType, targetId);

        return saved;
    }

    /**
     * 커뮤니티의 모든 모더레이션 로그 조회 (페이징)
     * OWNER/ADMIN만 조회 가능
     */
    @Transactional(readOnly = true)
    public Page<ModerationLog> getCommunityLogs(Long communityId, Pageable pageable) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인
        CommunityRole role = communityMemberService.getMemberRole(communityId, currentUser.getId());
        if (role != CommunityRole.OWNER && role != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return moderationLogRepository.findByCommunityIdWithModerator(communityId, pageable);
    }

    /**
     * 특정 액션 타입별 로그 조회
     */
    @Transactional(readOnly = true)
    public Page<ModerationLog> getLogsByActionType(Long communityId, ModerationActionType actionType, Pageable pageable) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인
        CommunityRole role = communityMemberService.getMemberRole(communityId, currentUser.getId());
        if (role != CommunityRole.OWNER && role != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return moderationLogRepository.findByCommunityIdAndActionType(communityId, actionType, pageable);
    }

    /**
     * 특정 모더레이터의 활동 로그 조회
     */
    @Transactional(readOnly = true)
    public List<ModerationLog> getLogsByModerator(Long communityId, Long moderatorId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인
        CommunityRole role = communityMemberService.getMemberRole(communityId, currentUser.getId());
        if (role != CommunityRole.OWNER && role != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return moderationLogRepository.findByCommunityIdAndModeratorId(communityId, moderatorId);
    }

    /**
     * 특정 대상에 대한 모더레이션 이력 조회
     */
    @Transactional(readOnly = true)
    public List<ModerationLog> getLogsByTarget(Long communityId, String targetType, Long targetId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인
        CommunityRole role = communityMemberService.getMemberRole(communityId, currentUser.getId());
        if (role != CommunityRole.OWNER && role != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return moderationLogRepository.findByCommunityIdAndTargetTypeAndTargetId(
                communityId, targetType, targetId
        );
    }

    /**
     * 최근 모더레이션 활동 조회
     */
    @Transactional(readOnly = true)
    public List<ModerationLog> getRecentLogs(Long communityId, int limit) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인
        CommunityRole role = communityMemberService.getMemberRole(communityId, currentUser.getId());
        if (role != CommunityRole.OWNER && role != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Pageable pageable = PageRequest.of(0, limit);
        return moderationLogRepository.findRecentLogsByCommunity(communityId, pageable);
    }

    /**
     * 특정 기간 내 모더레이션 로그 수 조회
     */
    @Transactional(readOnly = true)
    public long countLogsByPeriod(Long communityId, LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인
        CommunityRole role = communityMemberService.getMemberRole(communityId, currentUser.getId());
        if (role != CommunityRole.OWNER && role != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return moderationLogRepository.countByCommunityIdAndCreatedAtBetween(
                communityId, startDate, endDate
        );
    }

    /**
     * 로그 단건 조회
     */
    @Transactional(readOnly = true)
    public ModerationLog getLogById(Long logId) {
        return moderationLogRepository.findById(logId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }
}

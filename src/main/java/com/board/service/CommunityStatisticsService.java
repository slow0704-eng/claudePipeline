package com.board.service;

import com.board.entity.Community;
import com.board.entity.CommunityMember;
import com.board.enums.CommunityRole;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.BoardRepository;
import com.board.repository.CommunityMemberRepository;
import com.board.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 커뮤니티 통계 서비스
 * 커뮤니티 활동 통계 및 분석 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityStatisticsService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final BoardRepository boardRepository;

    /**
     * 커뮤니티 전체 통계 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCommunityStatistics(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        Map<String, Object> stats = new HashMap<>();

        // 기본 정보
        stats.put("communityId", communityId);
        stats.put("name", community.getName());
        stats.put("type", community.getType());
        stats.put("createdAt", community.getCreatedAt());

        // 멤버 통계
        stats.put("totalMembers", community.getMemberCount());
        stats.put("ownerCount", communityMemberRepository.countByCommunityIdAndRole(communityId, CommunityRole.OWNER));
        stats.put("adminCount", communityMemberRepository.countByCommunityIdAndRole(communityId, CommunityRole.ADMIN));
        stats.put("memberCount", communityMemberRepository.countByCommunityIdAndRole(communityId, CommunityRole.MEMBER));

        // 게시글 통계
        stats.put("totalBoards", community.getBoardCount());
        stats.put("publishedBoards", boardRepository.countByCommunityIdAndIsDraftFalse(communityId));
        stats.put("draftBoards", boardRepository.countByCommunityIdAndIsDraftTrue(communityId));

        // 활동 통계 (최근 7일, 30일)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        stats.put("recentBoards7Days", boardRepository.countByCommunityIdAndCreatedAtAfter(communityId, sevenDaysAgo));
        stats.put("recentBoards30Days", boardRepository.countByCommunityIdAndCreatedAtAfter(communityId, thirtyDaysAgo));
        stats.put("recentMembers7Days", communityMemberRepository.countByCommunityIdAndJoinedAtAfter(communityId, sevenDaysAgo));
        stats.put("recentMembers30Days", communityMemberRepository.countByCommunityIdAndJoinedAtAfter(communityId, thirtyDaysAgo));

        // 활성도 점수 계산 (간단한 알고리즘)
        double activityScore = calculateActivityScore(
                (Long) stats.get("recentBoards7Days"),
                (Long) stats.get("recentMembers7Days"),
                community.getMemberCount()
        );
        stats.put("activityScore", activityScore);

        return stats;
    }

    /**
     * 커뮤니티 활성도 점수 계산
     * 최근 7일 게시글 수 + 최근 7일 가입자 수 + 전체 멤버 수 / 10
     */
    private double calculateActivityScore(long recentBoards, long recentMembers, int totalMembers) {
        return (recentBoards * 2.0) + (recentMembers * 1.5) + (totalMembers / 10.0);
    }

    /**
     * 커뮤니티 멤버 성장 추이 (최근 30일)
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getMemberGrowthTrend(Long communityId) {
        Map<String, Long> trend = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 30; i++) {
            LocalDateTime date = now.minusDays(i);
            LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

            long count = communityMemberRepository.countByCommunityIdAndJoinedAtBetween(
                    communityId, startOfDay, endOfDay
            );

            trend.put(date.toLocalDate().toString(), count);
        }

        return trend;
    }

    /**
     * 커뮤니티 게시글 작성 추이 (최근 30일)
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getBoardCreationTrend(Long communityId) {
        Map<String, Long> trend = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 30; i++) {
            LocalDateTime date = now.minusDays(i);
            LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

            long count = boardRepository.countByCommunityIdAndCreatedAtBetween(
                    communityId, startOfDay, endOfDay
            );

            trend.put(date.toLocalDate().toString(), count);
        }

        return trend;
    }

    /**
     * 상위 활동 멤버 조회 (게시글 작성 수 기준)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopActiveMembers(Long communityId, int limit) {
        // 커뮤니티 존재 확인
        communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        List<Object[]> results = boardRepository.findTopActiveMembersByCommunity(communityId);

        return results.stream()
                .limit(limit)
                .map(result -> {
                    Map<String, Object> member = new HashMap<>();
                    member.put("userId", result[0]);
                    member.put("nickname", result[1]);
                    member.put("boardCount", result[2]);
                    return member;
                })
                .toList();
    }

    /**
     * 커뮤니티 순위 정보 (전체 커뮤니티 대비)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCommunityRanking(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        Map<String, Object> ranking = new HashMap<>();

        // 멤버 수 순위
        long memberRank = communityRepository.countByMemberCountGreaterThan(community.getMemberCount()) + 1;
        ranking.put("memberRank", memberRank);

        // 게시글 수 순위
        long boardRank = communityRepository.countByBoardCountGreaterThan(community.getBoardCount()) + 1;
        ranking.put("boardRank", boardRank);

        // 전체 커뮤니티 수
        long totalCommunities = communityRepository.countByIsActiveTrue();
        ranking.put("totalCommunities", totalCommunities);

        // 상위 퍼센트 계산
        double memberPercentile = ((double) memberRank / totalCommunities) * 100;
        double boardPercentile = ((double) boardRank / totalCommunities) * 100;

        ranking.put("memberPercentile", String.format("%.1f", memberPercentile));
        ranking.put("boardPercentile", String.format("%.1f", boardPercentile));

        return ranking;
    }

    /**
     * 커뮤니티 요약 정보 (대시보드용)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCommunitySummary(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        Map<String, Object> summary = new HashMap<>();

        summary.put("name", community.getName());
        summary.put("type", community.getType());
        summary.put("memberCount", community.getMemberCount());
        summary.put("boardCount", community.getBoardCount());
        summary.put("isActive", community.getIsActive());
        summary.put("createdAt", community.getCreatedAt());

        // 최근 활동
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long recentActivity = boardRepository.countByCommunityIdAndCreatedAtAfter(communityId, sevenDaysAgo);
        summary.put("recentActivity", recentActivity);

        // 활성도 레벨 계산 (LOW, MEDIUM, HIGH)
        String activityLevel = calculateActivityLevel(recentActivity, community.getMemberCount());
        summary.put("activityLevel", activityLevel);

        return summary;
    }

    /**
     * 활성도 레벨 계산
     */
    private String calculateActivityLevel(long recentBoards, int memberCount) {
        if (memberCount == 0) return "INACTIVE";

        double avgBoardsPerMember = (double) recentBoards / memberCount;

        if (avgBoardsPerMember >= 0.5) return "HIGH";
        if (avgBoardsPerMember >= 0.2) return "MEDIUM";
        if (avgBoardsPerMember >= 0.05) return "LOW";
        return "INACTIVE";
    }
}

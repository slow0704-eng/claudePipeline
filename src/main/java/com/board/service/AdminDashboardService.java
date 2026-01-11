package com.board.service;

import com.board.entity.*;
import com.board.enums.CommunityType;
import com.board.enums.ReportStatus;
import com.board.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 관리자 대시보드 통계 서비스
 * 전체 사이트 통계 및 모니터링 데이터 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    /**
     * 대시보드 전체 통계 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 실시간 지표
        stats.put("realTimeMetrics", getRealTimeMetrics());

        // 트렌드 데이터
        stats.put("trendData", getTrendData());

        // 최근 활동
        stats.put("recentActivity", getRecentActivity());

        // 인기 게시글
        stats.put("popularBoards", getPopularBoards());

        // 커뮤니티 통계
        stats.put("communityStats", getCommunityStats());

        return stats;
    }

    /**
     * 실시간 지표 조회
     */
    private Map<String, Object> getRealTimeMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime yesterdayStart = todayStart.minusDays(1);
        LocalDateTime thirtyMinutesAgo = now.minusMinutes(30);

        // 활성 사용자 (30분 이내 활동)
        long activeUsers = userRepository.countByLastLoginAtAfter(thirtyMinutesAgo);
        metrics.put("activeUsers30min", activeUsers);

        // 오늘 신규 가입자
        long todayNewUsers = userRepository.countByCreatedAtAfter(todayStart);
        long yesterdayNewUsers = userRepository.countByCreatedAtBetween(yesterdayStart, todayStart);
        metrics.put("todayNewUsers", todayNewUsers);
        metrics.put("userGrowthRate", calculateGrowthRate(todayNewUsers, yesterdayNewUsers));

        // 오늘 작성 게시글
        long todayNewBoards = boardRepository.countByCreatedAtAfter(todayStart);
        long yesterdayNewBoards = boardRepository.countByCreatedAtBetween(yesterdayStart, todayStart);
        metrics.put("todayNewBoards", todayNewBoards);
        metrics.put("boardGrowthRate", calculateGrowthRate(todayNewBoards, yesterdayNewBoards));

        // 오늘 작성 댓글
        long todayNewComments = commentRepository.countByCreatedAtAfter(todayStart);
        long yesterdayNewComments = commentRepository.countByCreatedAtBetween(yesterdayStart, todayStart);
        metrics.put("todayNewComments", todayNewComments);
        metrics.put("commentGrowthRate", calculateGrowthRate(todayNewComments, yesterdayNewComments));

        // 미처리 신고
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);
        metrics.put("pendingReports", pendingReports);

        // 긴급 신고 (CRITICAL 우선순위)
        metrics.put("urgentReports", 0L); // 우선순위 필드가 없으면 0

        // 서버 상태
        metrics.put("serverStatus", getServerStatus());

        return metrics;
    }

    /**
     * 증감율 계산
     */
    private double calculateGrowthRate(long current, long previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((double) (current - previous) / previous) * 100.0;
    }

    /**
     * 서버 상태 조회
     */
    private Map<String, Object> getServerStatus() {
        Map<String, Object> status = new HashMap<>();

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsage = ((double) usedMemory / totalMemory) * 100;

        status.put("status", memoryUsage < 70 ? "NORMAL" : (memoryUsage < 85 ? "WARNING" : "CRITICAL"));
        status.put("memoryUsage", memoryUsage);
        status.put("totalMemory", totalMemory / (1024 * 1024)); // MB
        status.put("usedMemory", usedMemory / (1024 * 1024)); // MB

        return status;
    }

    /**
     * 트렌드 데이터 조회 (7일, 30일)
     */
    private Map<String, Object> getTrendData() {
        Map<String, Object> trendData = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        // 7일 트렌드
        List<String> last7Days = new ArrayList<>();
        List<Long> userTrend7Days = new ArrayList<>();
        List<Long> boardTrend7Days = new ArrayList<>();
        List<Long> commentTrend7Days = new ArrayList<>();
        List<Long> reportTrend7Days = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            last7Days.add(date.format(formatter));
            userTrend7Days.add(userRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            boardTrend7Days.add(boardRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            commentTrend7Days.add(commentRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            reportTrend7Days.add(reportRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }

        trendData.put("last7Days", last7Days);
        trendData.put("userTrend7Days", userTrend7Days);
        trendData.put("boardTrend7Days", boardTrend7Days);
        trendData.put("commentTrend7Days", commentTrend7Days);
        trendData.put("reportTrend7Days", reportTrend7Days);

        // 30일 트렌드
        List<String> last30Days = new ArrayList<>();
        List<Long> userTrend30Days = new ArrayList<>();
        List<Long> boardTrend30Days = new ArrayList<>();
        List<Long> commentTrend30Days = new ArrayList<>();
        List<Long> reportTrend30Days = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            last30Days.add(date.format(formatter));
            userTrend30Days.add(userRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            boardTrend30Days.add(boardRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            commentTrend30Days.add(commentRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            reportTrend30Days.add(reportRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }

        trendData.put("last30Days", last30Days);
        trendData.put("userTrend30Days", userTrend30Days);
        trendData.put("boardTrend30Days", boardTrend30Days);
        trendData.put("commentTrend30Days", commentTrend30Days);
        trendData.put("reportTrend30Days", reportTrend30Days);

        return trendData;
    }

    /**
     * 최근 활동 조회
     */
    private Map<String, Object> getRecentActivity() {
        Map<String, Object> activity = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");

        // 최근 게시글
        List<Board> recentBoards = boardRepository.findTop10ByOrderByCreatedAtDesc();
        activity.put("recentBoards", recentBoards.stream().map(board -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", board.getId());
            map.put("title", truncate(board.getTitle(), 30));
            map.put("author", board.getAuthor().getNickname());
            map.put("viewCount", board.getViewCount());
            map.put("likeCount", board.getLikeCount());
            map.put("createdAt", board.getCreatedAt().format(formatter));
            return map;
        }).toList());

        // 최근 댓글
        List<Comment> recentComments = commentRepository.findTop10ByOrderByCreatedAtDesc();
        activity.put("recentComments", recentComments.stream().map(comment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("boardId", comment.getBoard().getId());
            map.put("content", truncate(comment.getContent(), 50));
            map.put("author", comment.getAuthor().getNickname());
            map.put("boardTitle", truncate(comment.getBoard().getTitle(), 20));
            map.put("createdAt", comment.getCreatedAt().format(formatter));
            return map;
        }).toList());

        // 최근 신고
        List<Report> recentReports = reportRepository.findTop10ByStatusOrderByCreatedAtDesc(ReportStatus.PENDING);
        activity.put("recentReports", recentReports.stream().map(report -> {
            Map<String, Object> map = new HashMap<>();
            map.put("targetType", report.getTargetType().name());
            map.put("reportType", report.getReportType().name());
            map.put("reporter", report.getReporter().getNickname());
            map.put("reason", truncate(report.getReason(), 30));
            map.put("priority", "NORMAL"); // 기본 우선순위
            return map;
        }).toList());

        // 최근 가입자
        List<User> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc();
        activity.put("recentUsers", recentUsers.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("nickname", user.getNickname());
            map.put("email", user.getEmail() != null ? user.getEmail() : "");
            map.put("createdAt", user.getCreatedAt().format(formatter));
            return map;
        }).toList());

        return activity;
    }

    /**
     * 인기 게시글 조회 (최근 30일)
     */
    private List<Map<String, Object>> getPopularBoards() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 인기도 점수 = 조회수 + (좋아요 * 5) + (댓글 * 3)
        List<Board> boards = boardRepository.findTop10ByCreatedAtAfterOrderByViewCountDesc(thirtyDaysAgo);

        return boards.stream()
            .sorted((b1, b2) -> {
                long score1 = b1.getViewCount() + (b1.getLikeCount() * 5) + (b1.getCommentCount() * 3);
                long score2 = b2.getViewCount() + (b2.getLikeCount() * 5) + (b2.getCommentCount() * 3);
                return Long.compare(score2, score1);
            })
            .limit(10)
            .map(board -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", board.getId());
                map.put("title", truncate(board.getTitle(), 40));
                map.put("author", board.getAuthor().getNickname());
                map.put("viewCount", board.getViewCount());
                map.put("likeCount", board.getLikeCount());
                map.put("commentCount", board.getCommentCount());
                map.put("popularityScore", board.getViewCount() + (board.getLikeCount() * 5) + (board.getCommentCount() * 3));
                return map;
            })
            .toList();
    }

    /**
     * 커뮤니티 통계 조회
     */
    private Map<String, Object> getCommunityStats() {
        Map<String, Object> stats = new HashMap<>();

        // 전체 통계
        long totalCommunities = communityRepository.count();
        long activeCommunities = communityRepository.countByIsActive(true);
        long inactiveCommunities = communityRepository.countByIsActive(false);

        stats.put("totalCommunities", totalCommunities);
        stats.put("activeCommunities", activeCommunities);
        stats.put("inactiveCommunities", inactiveCommunities);

        // 타입별 통계
        long publicCount = communityRepository.countByType(CommunityType.PUBLIC);
        long privateCount = communityRepository.countByType(CommunityType.PRIVATE);
        long secretCount = communityRepository.countByType(CommunityType.SECRET);

        stats.put("publicCount", publicCount);
        stats.put("privateCount", privateCount);
        stats.put("secretCount", secretCount);

        // 최근 생성 통계
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        stats.put("recentCommunities7Days", communityRepository.countByCreatedAtAfter(sevenDaysAgo));
        stats.put("recentCommunities30Days", communityRepository.countByCreatedAtAfter(thirtyDaysAgo));

        // 상위 커뮤니티 (멤버 수, 게시글 수)
        stats.put("topByMembers", communityRepository.findTop5ByOrderByMemberCountDesc().stream()
            .map(c -> Map.of("id", c.getId(), "name", c.getName(), "memberCount", c.getMemberCount()))
            .toList());

        stats.put("topByBoards", communityRepository.findTop5ByOrderByBoardCountDesc().stream()
            .map(c -> Map.of("id", c.getId(), "name", c.getName(), "boardCount", c.getBoardCount()))
            .toList());

        return stats;
    }

    /**
     * 종합 통계 대시보드 데이터
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getComprehensiveStats() {
        Map<String, Object> stats = new HashMap<>();

        // 전체 현황
        stats.put("totalUsers", userRepository.count());
        stats.put("totalBoards", boardRepository.count());
        stats.put("totalComments", commentRepository.count());
        stats.put("totalCommunities", communityRepository.count());
        stats.put("totalReports", reportRepository.count());

        // 사용자 통계
        stats.put("userStats", getUserStats());

        // 게시글 통계
        stats.put("boardStats", getBoardStats());

        // 커뮤니티 통계
        stats.put("communityStats", getCommunityStats());

        // 신고 통계
        stats.put("reportStats", getReportStats());

        return stats;
    }

    private Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        stats.put("newUsersLast30Days", userRepository.countByCreatedAtAfter(thirtyDaysAgo));
        stats.put("activeUsersLast30Days", userRepository.countByLastLoginAtAfter(thirtyDaysAgo));
        stats.put("totalEnabledUsers", userRepository.countByEnabled(true));
        stats.put("totalDisabledUsers", userRepository.countByEnabled(false));

        return stats;
    }

    private Map<String, Object> getBoardStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        stats.put("newBoardsLast30Days", boardRepository.countByCreatedAtAfter(thirtyDaysAgo));
        stats.put("totalViews", boardRepository.sumViewCount());
        stats.put("totalLikes", boardRepository.sumLikeCount());
        stats.put("avgViewsPerBoard", boardRepository.avgViewCount());

        return stats;
    }

    private Map<String, Object> getReportStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("pendingReports", reportRepository.countByStatus(ReportStatus.PENDING));
        stats.put("approvedReports", reportRepository.countByStatus(ReportStatus.APPROVED));
        stats.put("rejectedReports", reportRepository.countByStatus(ReportStatus.REJECTED));

        return stats;
    }

    /**
     * 문자열 자르기
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }
}

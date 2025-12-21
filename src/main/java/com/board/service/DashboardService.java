package com.board.service;

import com.board.dto.DashboardStats;
import com.board.entity.Board;
import com.board.entity.Comment;
import com.board.entity.Report;
import com.board.entity.User;
import com.board.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 관리자 대시보드 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final LikeRepository likeRepository;

    /**
     * 대시보드 전체 통계 조회
     */
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        stats.setRealTimeMetrics(getRealTimeMetrics());
        stats.setTrendData(getTrendData());
        stats.setRecentActivity(getRecentActivity());
        stats.setPopularBoards(getPopularBoards());

        return stats;
    }

    /**
     * 실시간 지표 계산
     */
    private DashboardStats.RealTimeMetrics getRealTimeMetrics() {
        DashboardStats.RealTimeMetrics metrics = new DashboardStats.RealTimeMetrics();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.toLocalDate().atStartOfDay();
        LocalDateTime yesterday = today.minusDays(1);

        // 활성 사용자 수 (최근 활동 기준)
        metrics.setActiveUsers5min(countActiveUsers(now.minusMinutes(5)));
        metrics.setActiveUsers15min(countActiveUsers(now.minusMinutes(15)));
        metrics.setActiveUsers30min(countActiveUsers(now.minusMinutes(30)));

        // 오늘 통계
        metrics.setTodayNewUsers(userRepository.countByCreatedAtAfter(today));
        metrics.setTodayNewBoards(boardRepository.countByCreatedAtAfter(today));
        metrics.setTodayNewComments(commentRepository.countByCreatedAtAfter(today));
        metrics.setTodayLikes(likeRepository.countByCreatedAtAfter(today));

        // 어제 통계 (증감율 계산용)
        long yesterdayNewUsers = userRepository.countByCreatedAtBetween(yesterday, today);
        long yesterdayNewBoards = boardRepository.countByCreatedAtBetween(yesterday, today);
        long yesterdayNewComments = commentRepository.countByCreatedAtBetween(yesterday, today);

        // 증감율 계산
        metrics.setUserGrowthRate(calculateGrowthRate(yesterdayNewUsers, metrics.getTodayNewUsers()));
        metrics.setBoardGrowthRate(calculateGrowthRate(yesterdayNewBoards, metrics.getTodayNewBoards()));
        metrics.setCommentGrowthRate(calculateGrowthRate(yesterdayNewComments, metrics.getTodayNewComments()));

        // 신고
        metrics.setPendingReports(reportRepository.countByStatus("PENDING"));
        metrics.setUrgentReports(countUrgentReports());

        // 서버 상태 (간단히 구현)
        DashboardStats.ServerStatus serverStatus = new DashboardStats.ServerStatus();
        serverStatus.setCpuUsage(getSystemCpuUsage());
        serverStatus.setMemoryUsage(getSystemMemoryUsage());
        serverStatus.setDiskUsage(0.0);  // 추후 구현
        serverStatus.setStatus(determineServerStatus(serverStatus));
        metrics.setServerStatus(serverStatus);

        return metrics;
    }

    /**
     * 트렌드 데이터 계산
     */
    private DashboardStats.TrendData getTrendData() {
        DashboardStats.TrendData trendData = new DashboardStats.TrendData();

        // 최근 7일 트렌드
        List<String> last7Days = new ArrayList<>();
        List<Long> userTrend7 = new ArrayList<>();
        List<Long> boardTrend7 = new ArrayList<>();
        List<Long> commentTrend7 = new ArrayList<>();
        List<Long> reportTrend7 = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            last7Days.add(date.format(formatter));
            userTrend7.add(userRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            boardTrend7.add(boardRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            commentTrend7.add(commentRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            reportTrend7.add(reportRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }

        trendData.setLast7Days(last7Days);
        trendData.setUserTrend7Days(userTrend7);
        trendData.setBoardTrend7Days(boardTrend7);
        trendData.setCommentTrend7Days(commentTrend7);
        trendData.setReportTrend7Days(reportTrend7);

        // 최근 30일 트렌드
        List<String> last30Days = new ArrayList<>();
        List<Long> userTrend30 = new ArrayList<>();
        List<Long> boardTrend30 = new ArrayList<>();
        List<Long> commentTrend30 = new ArrayList<>();
        List<Long> reportTrend30 = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            last30Days.add(date.format(formatter));
            userTrend30.add(userRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            boardTrend30.add(boardRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            commentTrend30.add(commentRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            reportTrend30.add(reportRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }

        trendData.setLast30Days(last30Days);
        trendData.setUserTrend30Days(userTrend30);
        trendData.setBoardTrend30Days(boardTrend30);
        trendData.setCommentTrend30Days(commentTrend30);
        trendData.setReportTrend30Days(reportTrend30);

        // 시간대별 활동 히트맵 (최근 7일 × 24시간)
        List<List<Long>> heatmap = new ArrayList<>();
        for (int day = 6; day >= 0; day--) {
            List<Long> hourlyData = new ArrayList<>();
            LocalDate date = today.minusDays(day);

            for (int hour = 0; hour < 24; hour++) {
                LocalDateTime hourStart = date.atTime(hour, 0);
                LocalDateTime hourEnd = hourStart.plusHours(1);

                // 해당 시간대의 활동량 (게시글 + 댓글)
                long activity = boardRepository.countByCreatedAtBetween(hourStart, hourEnd)
                              + commentRepository.countByCreatedAtBetween(hourStart, hourEnd);
                hourlyData.add(activity);
            }
            heatmap.add(hourlyData);
        }
        trendData.setHourlyActivityHeatmap(heatmap);

        return trendData;
    }

    /**
     * 최근 활동 조회
     */
    private DashboardStats.RecentActivity getRecentActivity() {
        DashboardStats.RecentActivity activity = new DashboardStats.RecentActivity();

        // 최근 게시글 5건
        List<Board> recentBoards = boardRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        activity.setRecentBoards(recentBoards.stream()
            .map(this::toRecentBoard)
            .collect(Collectors.toList()));

        // 최근 댓글 5건
        List<Comment> recentComments = commentRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        activity.setRecentComments(recentComments.stream()
            .map(this::toRecentComment)
            .collect(Collectors.toList()));

        // 최근 미처리 신고
        List<Report> recentReports = reportRepository.findByStatus("PENDING",
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        activity.setRecentReports(recentReports.stream()
            .map(this::toRecentReport)
            .collect(Collectors.toList()));

        // 최근 가입자 5명
        List<User> recentUsers = userRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        activity.setRecentUsers(recentUsers.stream()
            .map(this::toRecentUser)
            .collect(Collectors.toList()));

        return activity;
    }

    /**
     * 인기 게시글 TOP 10
     */
    private List<DashboardStats.PopularBoard> getPopularBoards() {
        // 최근 30일 게시글 중 인기도 계산
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Board> boards = boardRepository.findByCreatedAtAfter(thirtyDaysAgo);

        return boards.stream()
            .map(this::toPopularBoard)
            .sorted((a, b) -> Double.compare(b.getPopularityScore(), a.getPopularityScore()))
            .limit(10)
            .collect(Collectors.toList());
    }

    // === 헬퍼 메서드 ===

    /**
     * 활성 사용자 수 추정 (최근 활동 기준)
     */
    private long countActiveUsers(LocalDateTime since) {
        // 최근 게시글 작성자 + 댓글 작성자 수 (중복 제거)
        long boardAuthors = boardRepository.countByCreatedAtAfter(since);
        long commentAuthors = commentRepository.countByCreatedAtAfter(since);

        // 간단한 추정 (실제로는 distinct user count 필요)
        return boardAuthors + commentAuthors;
    }

    /**
     * 긴급 신고 수 계산
     */
    private long countUrgentReports() {
        // 우선순위 로직 추후 구현
        // 여기서는 PENDING 상태인 것의 절반을 긴급으로 가정
        long pending = reportRepository.countByStatus("PENDING");
        return pending / 2;
    }

    /**
     * 증감율 계산
     */
    private double calculateGrowthRate(long previous, long current) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((double) (current - previous) / previous) * 100.0;
    }

    /**
     * 시스템 CPU 사용률 (간단한 구현)
     */
    private double getSystemCpuUsage() {
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        // 간단한 추정 (실제로는 OperatingSystemMXBean 사용)
        return Math.random() * 100; // 임시 랜덤값
    }

    /**
     * 시스템 메모리 사용률
     */
    private double getSystemMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return ((double) usedMemory / totalMemory) * 100.0;
    }

    /**
     * 서버 상태 판정
     */
    private String determineServerStatus(DashboardStats.ServerStatus status) {
        if (status.getCpuUsage() > 90 || status.getMemoryUsage() > 90) {
            return "CRITICAL";
        } else if (status.getCpuUsage() > 70 || status.getMemoryUsage() > 70) {
            return "WARNING";
        }
        return "NORMAL";
    }

    // === DTO 변환 메서드 ===

    private DashboardStats.RecentBoard toRecentBoard(Board board) {
        DashboardStats.RecentBoard dto = new DashboardStats.RecentBoard();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setAuthor(board.getNickname() != null ? board.getNickname() : board.getAuthor());
        dto.setCreatedAt(formatDateTime(board.getCreatedAt()));
        dto.setViewCount(board.getViewCount());
        dto.setLikeCount(board.getLikeCount());
        return dto;
    }

    private DashboardStats.RecentComment toRecentComment(Comment comment) {
        DashboardStats.RecentComment dto = new DashboardStats.RecentComment();
        dto.setId(comment.getId());
        dto.setContent(truncate(comment.getContent(), 50));
        dto.setAuthor(comment.getNickname() != null ? comment.getNickname() : "알 수 없음");
        dto.setBoardId(comment.getBoardId());

        // Board 제목 조회
        String boardTitle = "";
        if (comment.getBoardId() != null) {
            try {
                Board board = boardRepository.findById(comment.getBoardId()).orElse(null);
                if (board != null) {
                    boardTitle = truncate(board.getTitle(), 30);
                }
            } catch (Exception e) {
                // 조회 실패 시 빈 문자열
            }
        }
        dto.setBoardTitle(boardTitle);
        dto.setCreatedAt(formatDateTime(comment.getCreatedAt()));
        return dto;
    }

    private DashboardStats.RecentReport toRecentReport(Report report) {
        DashboardStats.RecentReport dto = new DashboardStats.RecentReport();
        dto.setId(report.getId());
        dto.setTargetType(report.getTargetType().toString());
        dto.setReportType(report.getTargetType().toString());  // targetType과 동일
        dto.setReason(truncate(report.getReason().toString(), 50));

        // Reporter 닉네임 조회
        String reporterNickname = "알 수 없음";
        if (report.getReporterId() != null) {
            try {
                User reporter = userRepository.findById(report.getReporterId()).orElse(null);
                if (reporter != null) {
                    reporterNickname = reporter.getNickname();
                }
            } catch (Exception e) {
                // 조회 실패 시 기본값 사용
            }
        }
        dto.setReporter(reporterNickname);
        dto.setCreatedAt(formatDateTime(report.getCreatedAt()));
        dto.setPriority("MEDIUM");  // 우선순위 로직 추후 구현
        return dto;
    }

    private DashboardStats.RecentUser toRecentUser(User user) {
        DashboardStats.RecentUser dto = new DashboardStats.RecentUser();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setEmail(maskEmail(user.getEmail()));
        dto.setCreatedAt(formatDateTime(user.getCreatedAt()));
        return dto;
    }

    private DashboardStats.PopularBoard toPopularBoard(Board board) {
        DashboardStats.PopularBoard dto = new DashboardStats.PopularBoard();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setAuthor(board.getNickname() != null ? board.getNickname() : board.getAuthor());
        dto.setViewCount(board.getViewCount());
        dto.setLikeCount(board.getLikeCount());
        dto.setCommentCount(board.getCommentCount());
        dto.setCreatedAt(formatDateTime(board.getCreatedAt()));

        // 인기도 점수 = 조회수*1 + 좋아요*5 + 댓글*3
        double score = (board.getViewCount() != null ? board.getViewCount() : 0) * 1.0
                     + (board.getLikeCount() != null ? board.getLikeCount() : 0) * 5.0
                     + (board.getCommentCount() != null ? board.getCommentCount() : 0) * 3.0;
        dto.setPopularityScore(score);

        return dto;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "";
        String[] parts = email.split("@");
        String localPart = parts[0];
        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***@" + parts[1];
        }
        return localPart.substring(0, 2) + "***@" + parts[1];
    }
}

package com.board.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 관리자 대시보드 통계 DTO
 */
@Data
public class DashboardStats {

    // 실시간 지표
    private RealTimeMetrics realTimeMetrics;

    // 트렌드 데이터
    private TrendData trendData;

    // 최근 활동
    private RecentActivity recentActivity;

    // 인기 게시글
    private List<PopularBoard> popularBoards;

    /**
     * 실시간 지표
     */
    @Data
    public static class RealTimeMetrics {
        // 현재 접속자 수 (추정)
        private long activeUsers5min;
        private long activeUsers15min;
        private long activeUsers30min;

        // 오늘 통계
        private long todayNewUsers;
        private long todayNewBoards;
        private long todayNewComments;
        private long todayLikes;

        // 신고
        private long pendingReports;
        private long urgentReports;  // 긴급도 HIGH 이상

        // 전일 대비 증감율
        private double userGrowthRate;      // 신규 가입자 증감율
        private double boardGrowthRate;     // 게시글 증감율
        private double commentGrowthRate;   // 댓글 증감율

        // 서버 상태 (추후 구현)
        private ServerStatus serverStatus;
    }

    /**
     * 서버 상태
     */
    @Data
    public static class ServerStatus {
        private double cpuUsage;      // CPU 사용률 (%)
        private double memoryUsage;   // 메모리 사용률 (%)
        private double diskUsage;     // 디스크 사용률 (%)
        private String status;        // NORMAL, WARNING, CRITICAL
    }

    /**
     * 트렌드 데이터 (7일/30일)
     */
    @Data
    public static class TrendData {
        // 날짜별 데이터 (최근 7일)
        private List<String> last7Days;           // 날짜 라벨
        private List<Long> userTrend7Days;        // 신규 가입자
        private List<Long> boardTrend7Days;       // 게시글 작성
        private List<Long> commentTrend7Days;     // 댓글 작성
        private List<Long> reportTrend7Days;      // 신고 발생

        // 날짜별 데이터 (최근 30일)
        private List<String> last30Days;
        private List<Long> userTrend30Days;
        private List<Long> boardTrend30Days;
        private List<Long> commentTrend30Days;
        private List<Long> reportTrend30Days;

        // 시간대별 접속자 히트맵 (최근 7일, 24시간)
        private List<List<Long>> hourlyActivityHeatmap;  // [7일][24시간]
    }

    /**
     * 최근 활동
     */
    @Data
    public static class RecentActivity {
        private List<RecentBoard> recentBoards;        // 최근 게시글 5건
        private List<RecentComment> recentComments;    // 최근 댓글 5건
        private List<RecentReport> recentReports;      // 최근 신고 (미처리만)
        private List<RecentUser> recentUsers;          // 최근 가입자 5명
    }

    /**
     * 최근 게시글
     */
    @Data
    public static class RecentBoard {
        private Long id;
        private String title;
        private String author;
        private String createdAt;
        private Integer viewCount;
        private Integer likeCount;
    }

    /**
     * 최근 댓글
     */
    @Data
    public static class RecentComment {
        private Long id;
        private String content;
        private String author;
        private Long boardId;
        private String boardTitle;
        private String createdAt;
    }

    /**
     * 최근 신고
     */
    @Data
    public static class RecentReport {
        private Long id;
        private String targetType;
        private String reportType;
        private String reason;
        private String reporter;
        private String createdAt;
        private String priority;  // CRITICAL, HIGH, MEDIUM, LOW
    }

    /**
     * 최근 가입자
     */
    @Data
    public static class RecentUser {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String createdAt;
    }

    /**
     * 인기 게시글 TOP 10
     */
    @Data
    public static class PopularBoard {
        private Long id;
        private String title;
        private String author;
        private Integer viewCount;
        private Integer likeCount;
        private Integer commentCount;
        private String createdAt;
        private double popularityScore;  // 인기도 점수 (조회수*1 + 좋아요*5 + 댓글*3)
    }
}

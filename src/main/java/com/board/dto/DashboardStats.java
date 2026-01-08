package com.board.dto;

import java.util.List;
import java.util.Objects;

/**
 * 관리자 대시보드 통계 DTO
 */
public class DashboardStats {

    // 실시간 지표
    private RealTimeMetrics realTimeMetrics;

    // 트렌드 데이터
    private TrendData trendData;

    // 최근 활동
    private RecentActivity recentActivity;

    // 인기 게시글
    private List<PopularBoard> popularBoards;

    public RealTimeMetrics getRealTimeMetrics() {
        return realTimeMetrics;
    }

    public void setRealTimeMetrics(RealTimeMetrics realTimeMetrics) {
        this.realTimeMetrics = realTimeMetrics;
    }

    public TrendData getTrendData() {
        return trendData;
    }

    public void setTrendData(TrendData trendData) {
        this.trendData = trendData;
    }

    public RecentActivity getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(RecentActivity recentActivity) {
        this.recentActivity = recentActivity;
    }

    public List<PopularBoard> getPopularBoards() {
        return popularBoards;
    }

    public void setPopularBoards(List<PopularBoard> popularBoards) {
        this.popularBoards = popularBoards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardStats that = (DashboardStats) o;
        return Objects.equals(realTimeMetrics, that.realTimeMetrics) &&
                Objects.equals(trendData, that.trendData) &&
                Objects.equals(recentActivity, that.recentActivity) &&
                Objects.equals(popularBoards, that.popularBoards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(realTimeMetrics, trendData, recentActivity, popularBoards);
    }

    @Override
    public String toString() {
        return "DashboardStats{" +
                "realTimeMetrics=" + realTimeMetrics +
                ", trendData=" + trendData +
                ", recentActivity=" + recentActivity +
                ", popularBoards=" + popularBoards +
                '}';
    }

    /**
     * 실시간 지표
     */
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

        public long getActiveUsers5min() {
            return activeUsers5min;
        }

        public void setActiveUsers5min(long activeUsers5min) {
            this.activeUsers5min = activeUsers5min;
        }

        public long getActiveUsers15min() {
            return activeUsers15min;
        }

        public void setActiveUsers15min(long activeUsers15min) {
            this.activeUsers15min = activeUsers15min;
        }

        public long getActiveUsers30min() {
            return activeUsers30min;
        }

        public void setActiveUsers30min(long activeUsers30min) {
            this.activeUsers30min = activeUsers30min;
        }

        public long getTodayNewUsers() {
            return todayNewUsers;
        }

        public void setTodayNewUsers(long todayNewUsers) {
            this.todayNewUsers = todayNewUsers;
        }

        public long getTodayNewBoards() {
            return todayNewBoards;
        }

        public void setTodayNewBoards(long todayNewBoards) {
            this.todayNewBoards = todayNewBoards;
        }

        public long getTodayNewComments() {
            return todayNewComments;
        }

        public void setTodayNewComments(long todayNewComments) {
            this.todayNewComments = todayNewComments;
        }

        public long getTodayLikes() {
            return todayLikes;
        }

        public void setTodayLikes(long todayLikes) {
            this.todayLikes = todayLikes;
        }

        public long getPendingReports() {
            return pendingReports;
        }

        public void setPendingReports(long pendingReports) {
            this.pendingReports = pendingReports;
        }

        public long getUrgentReports() {
            return urgentReports;
        }

        public void setUrgentReports(long urgentReports) {
            this.urgentReports = urgentReports;
        }

        public double getUserGrowthRate() {
            return userGrowthRate;
        }

        public void setUserGrowthRate(double userGrowthRate) {
            this.userGrowthRate = userGrowthRate;
        }

        public double getBoardGrowthRate() {
            return boardGrowthRate;
        }

        public void setBoardGrowthRate(double boardGrowthRate) {
            this.boardGrowthRate = boardGrowthRate;
        }

        public double getCommentGrowthRate() {
            return commentGrowthRate;
        }

        public void setCommentGrowthRate(double commentGrowthRate) {
            this.commentGrowthRate = commentGrowthRate;
        }

        public ServerStatus getServerStatus() {
            return serverStatus;
        }

        public void setServerStatus(ServerStatus serverStatus) {
            this.serverStatus = serverStatus;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RealTimeMetrics that = (RealTimeMetrics) o;
            return activeUsers5min == that.activeUsers5min &&
                    activeUsers15min == that.activeUsers15min &&
                    activeUsers30min == that.activeUsers30min &&
                    todayNewUsers == that.todayNewUsers &&
                    todayNewBoards == that.todayNewBoards &&
                    todayNewComments == that.todayNewComments &&
                    todayLikes == that.todayLikes &&
                    pendingReports == that.pendingReports &&
                    urgentReports == that.urgentReports &&
                    Double.compare(that.userGrowthRate, userGrowthRate) == 0 &&
                    Double.compare(that.boardGrowthRate, boardGrowthRate) == 0 &&
                    Double.compare(that.commentGrowthRate, commentGrowthRate) == 0 &&
                    Objects.equals(serverStatus, that.serverStatus);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activeUsers5min, activeUsers15min, activeUsers30min, todayNewUsers,
                    todayNewBoards, todayNewComments, todayLikes, pendingReports, urgentReports,
                    userGrowthRate, boardGrowthRate, commentGrowthRate, serverStatus);
        }

        @Override
        public String toString() {
            return "RealTimeMetrics{" +
                    "activeUsers5min=" + activeUsers5min +
                    ", activeUsers15min=" + activeUsers15min +
                    ", activeUsers30min=" + activeUsers30min +
                    ", todayNewUsers=" + todayNewUsers +
                    ", todayNewBoards=" + todayNewBoards +
                    ", todayNewComments=" + todayNewComments +
                    ", todayLikes=" + todayLikes +
                    ", pendingReports=" + pendingReports +
                    ", urgentReports=" + urgentReports +
                    ", userGrowthRate=" + userGrowthRate +
                    ", boardGrowthRate=" + boardGrowthRate +
                    ", commentGrowthRate=" + commentGrowthRate +
                    ", serverStatus=" + serverStatus +
                    '}';
        }
    }

    /**
     * 서버 상태
     */
    public static class ServerStatus {
        private double cpuUsage;      // CPU 사용률 (%)
        private double memoryUsage;   // 메모리 사용률 (%)
        private double diskUsage;     // 디스크 사용률 (%)
        private String status;        // NORMAL, WARNING, CRITICAL

        public double getCpuUsage() {
            return cpuUsage;
        }

        public void setCpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        public double getMemoryUsage() {
            return memoryUsage;
        }

        public void setMemoryUsage(double memoryUsage) {
            this.memoryUsage = memoryUsage;
        }

        public double getDiskUsage() {
            return diskUsage;
        }

        public void setDiskUsage(double diskUsage) {
            this.diskUsage = diskUsage;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ServerStatus that = (ServerStatus) o;
            return Double.compare(that.cpuUsage, cpuUsage) == 0 &&
                    Double.compare(that.memoryUsage, memoryUsage) == 0 &&
                    Double.compare(that.diskUsage, diskUsage) == 0 &&
                    Objects.equals(status, that.status);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cpuUsage, memoryUsage, diskUsage, status);
        }

        @Override
        public String toString() {
            return "ServerStatus{" +
                    "cpuUsage=" + cpuUsage +
                    ", memoryUsage=" + memoryUsage +
                    ", diskUsage=" + diskUsage +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    /**
     * 트렌드 데이터 (7일/30일)
     */
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

        public List<String> getLast7Days() {
            return last7Days;
        }

        public void setLast7Days(List<String> last7Days) {
            this.last7Days = last7Days;
        }

        public List<Long> getUserTrend7Days() {
            return userTrend7Days;
        }

        public void setUserTrend7Days(List<Long> userTrend7Days) {
            this.userTrend7Days = userTrend7Days;
        }

        public List<Long> getBoardTrend7Days() {
            return boardTrend7Days;
        }

        public void setBoardTrend7Days(List<Long> boardTrend7Days) {
            this.boardTrend7Days = boardTrend7Days;
        }

        public List<Long> getCommentTrend7Days() {
            return commentTrend7Days;
        }

        public void setCommentTrend7Days(List<Long> commentTrend7Days) {
            this.commentTrend7Days = commentTrend7Days;
        }

        public List<Long> getReportTrend7Days() {
            return reportTrend7Days;
        }

        public void setReportTrend7Days(List<Long> reportTrend7Days) {
            this.reportTrend7Days = reportTrend7Days;
        }

        public List<String> getLast30Days() {
            return last30Days;
        }

        public void setLast30Days(List<String> last30Days) {
            this.last30Days = last30Days;
        }

        public List<Long> getUserTrend30Days() {
            return userTrend30Days;
        }

        public void setUserTrend30Days(List<Long> userTrend30Days) {
            this.userTrend30Days = userTrend30Days;
        }

        public List<Long> getBoardTrend30Days() {
            return boardTrend30Days;
        }

        public void setBoardTrend30Days(List<Long> boardTrend30Days) {
            this.boardTrend30Days = boardTrend30Days;
        }

        public List<Long> getCommentTrend30Days() {
            return commentTrend30Days;
        }

        public void setCommentTrend30Days(List<Long> commentTrend30Days) {
            this.commentTrend30Days = commentTrend30Days;
        }

        public List<Long> getReportTrend30Days() {
            return reportTrend30Days;
        }

        public void setReportTrend30Days(List<Long> reportTrend30Days) {
            this.reportTrend30Days = reportTrend30Days;
        }

        public List<List<Long>> getHourlyActivityHeatmap() {
            return hourlyActivityHeatmap;
        }

        public void setHourlyActivityHeatmap(List<List<Long>> hourlyActivityHeatmap) {
            this.hourlyActivityHeatmap = hourlyActivityHeatmap;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TrendData trendData = (TrendData) o;
            return Objects.equals(last7Days, trendData.last7Days) &&
                    Objects.equals(userTrend7Days, trendData.userTrend7Days) &&
                    Objects.equals(boardTrend7Days, trendData.boardTrend7Days) &&
                    Objects.equals(commentTrend7Days, trendData.commentTrend7Days) &&
                    Objects.equals(reportTrend7Days, trendData.reportTrend7Days) &&
                    Objects.equals(last30Days, trendData.last30Days) &&
                    Objects.equals(userTrend30Days, trendData.userTrend30Days) &&
                    Objects.equals(boardTrend30Days, trendData.boardTrend30Days) &&
                    Objects.equals(commentTrend30Days, trendData.commentTrend30Days) &&
                    Objects.equals(reportTrend30Days, trendData.reportTrend30Days) &&
                    Objects.equals(hourlyActivityHeatmap, trendData.hourlyActivityHeatmap);
        }

        @Override
        public int hashCode() {
            return Objects.hash(last7Days, userTrend7Days, boardTrend7Days, commentTrend7Days,
                    reportTrend7Days, last30Days, userTrend30Days, boardTrend30Days,
                    commentTrend30Days, reportTrend30Days, hourlyActivityHeatmap);
        }

        @Override
        public String toString() {
            return "TrendData{" +
                    "last7Days=" + last7Days +
                    ", userTrend7Days=" + userTrend7Days +
                    ", boardTrend7Days=" + boardTrend7Days +
                    ", commentTrend7Days=" + commentTrend7Days +
                    ", reportTrend7Days=" + reportTrend7Days +
                    ", last30Days=" + last30Days +
                    ", userTrend30Days=" + userTrend30Days +
                    ", boardTrend30Days=" + boardTrend30Days +
                    ", commentTrend30Days=" + commentTrend30Days +
                    ", reportTrend30Days=" + reportTrend30Days +
                    ", hourlyActivityHeatmap=" + hourlyActivityHeatmap +
                    '}';
        }
    }

    /**
     * 최근 활동
     */
    public static class RecentActivity {
        private List<RecentBoard> recentBoards;        // 최근 게시글 5건
        private List<RecentComment> recentComments;    // 최근 댓글 5건
        private List<RecentReport> recentReports;      // 최근 신고 (미처리만)
        private List<RecentUser> recentUsers;          // 최근 가입자 5명

        public List<RecentBoard> getRecentBoards() {
            return recentBoards;
        }

        public void setRecentBoards(List<RecentBoard> recentBoards) {
            this.recentBoards = recentBoards;
        }

        public List<RecentComment> getRecentComments() {
            return recentComments;
        }

        public void setRecentComments(List<RecentComment> recentComments) {
            this.recentComments = recentComments;
        }

        public List<RecentReport> getRecentReports() {
            return recentReports;
        }

        public void setRecentReports(List<RecentReport> recentReports) {
            this.recentReports = recentReports;
        }

        public List<RecentUser> getRecentUsers() {
            return recentUsers;
        }

        public void setRecentUsers(List<RecentUser> recentUsers) {
            this.recentUsers = recentUsers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecentActivity that = (RecentActivity) o;
            return Objects.equals(recentBoards, that.recentBoards) &&
                    Objects.equals(recentComments, that.recentComments) &&
                    Objects.equals(recentReports, that.recentReports) &&
                    Objects.equals(recentUsers, that.recentUsers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(recentBoards, recentComments, recentReports, recentUsers);
        }

        @Override
        public String toString() {
            return "RecentActivity{" +
                    "recentBoards=" + recentBoards +
                    ", recentComments=" + recentComments +
                    ", recentReports=" + recentReports +
                    ", recentUsers=" + recentUsers +
                    '}';
        }
    }

    /**
     * 최근 게시글
     */
    public static class RecentBoard {
        private Long id;
        private String title;
        private String author;
        private String createdAt;
        private Integer viewCount;
        private Integer likeCount;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getViewCount() {
            return viewCount;
        }

        public void setViewCount(Integer viewCount) {
            this.viewCount = viewCount;
        }

        public Integer getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(Integer likeCount) {
            this.likeCount = likeCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecentBoard that = (RecentBoard) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(title, that.title) &&
                    Objects.equals(author, that.author) &&
                    Objects.equals(createdAt, that.createdAt) &&
                    Objects.equals(viewCount, that.viewCount) &&
                    Objects.equals(likeCount, that.likeCount);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, title, author, createdAt, viewCount, likeCount);
        }

        @Override
        public String toString() {
            return "RecentBoard{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", viewCount=" + viewCount +
                    ", likeCount=" + likeCount +
                    '}';
        }
    }

    /**
     * 최근 댓글
     */
    public static class RecentComment {
        private Long id;
        private String content;
        private String author;
        private Long boardId;
        private String boardTitle;
        private String createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public Long getBoardId() {
            return boardId;
        }

        public void setBoardId(Long boardId) {
            this.boardId = boardId;
        }

        public String getBoardTitle() {
            return boardTitle;
        }

        public void setBoardTitle(String boardTitle) {
            this.boardTitle = boardTitle;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecentComment that = (RecentComment) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(content, that.content) &&
                    Objects.equals(author, that.author) &&
                    Objects.equals(boardId, that.boardId) &&
                    Objects.equals(boardTitle, that.boardTitle) &&
                    Objects.equals(createdAt, that.createdAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, content, author, boardId, boardTitle, createdAt);
        }

        @Override
        public String toString() {
            return "RecentComment{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    ", author='" + author + '\'' +
                    ", boardId=" + boardId +
                    ", boardTitle='" + boardTitle + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    '}';
        }
    }

    /**
     * 최근 신고
     */
    public static class RecentReport {
        private Long id;
        private String targetType;
        private String reportType;
        private String reason;
        private String reporter;
        private String createdAt;
        private String priority;  // CRITICAL, HIGH, MEDIUM, LOW

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public String getReportType() {
            return reportType;
        }

        public void setReportType(String reportType) {
            this.reportType = reportType;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getReporter() {
            return reporter;
        }

        public void setReporter(String reporter) {
            this.reporter = reporter;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecentReport that = (RecentReport) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(targetType, that.targetType) &&
                    Objects.equals(reportType, that.reportType) &&
                    Objects.equals(reason, that.reason) &&
                    Objects.equals(reporter, that.reporter) &&
                    Objects.equals(createdAt, that.createdAt) &&
                    Objects.equals(priority, that.priority);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, targetType, reportType, reason, reporter, createdAt, priority);
        }

        @Override
        public String toString() {
            return "RecentReport{" +
                    "id=" + id +
                    ", targetType='" + targetType + '\'' +
                    ", reportType='" + reportType + '\'' +
                    ", reason='" + reason + '\'' +
                    ", reporter='" + reporter + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", priority='" + priority + '\'' +
                    '}';
        }
    }

    /**
     * 최근 가입자
     */
    public static class RecentUser {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecentUser that = (RecentUser) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(username, that.username) &&
                    Objects.equals(nickname, that.nickname) &&
                    Objects.equals(email, that.email) &&
                    Objects.equals(createdAt, that.createdAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, username, nickname, email, createdAt);
        }

        @Override
        public String toString() {
            return "RecentUser{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", email='" + email + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    '}';
        }
    }

    /**
     * 인기 게시글 TOP 10
     */
    public static class PopularBoard {
        private Long id;
        private String title;
        private String author;
        private Integer viewCount;
        private Integer likeCount;
        private Integer commentCount;
        private String createdAt;
        private double popularityScore;  // 인기도 점수 (조회수*1 + 좋아요*5 + 댓글*3)

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public Integer getViewCount() {
            return viewCount;
        }

        public void setViewCount(Integer viewCount) {
            this.viewCount = viewCount;
        }

        public Integer getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(Integer likeCount) {
            this.likeCount = likeCount;
        }

        public Integer getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(Integer commentCount) {
            this.commentCount = commentCount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public double getPopularityScore() {
            return popularityScore;
        }

        public void setPopularityScore(double popularityScore) {
            this.popularityScore = popularityScore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PopularBoard that = (PopularBoard) o;
            return Double.compare(that.popularityScore, popularityScore) == 0 &&
                    Objects.equals(id, that.id) &&
                    Objects.equals(title, that.title) &&
                    Objects.equals(author, that.author) &&
                    Objects.equals(viewCount, that.viewCount) &&
                    Objects.equals(likeCount, that.likeCount) &&
                    Objects.equals(commentCount, that.commentCount) &&
                    Objects.equals(createdAt, that.createdAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, title, author, viewCount, likeCount, commentCount, createdAt, popularityScore);
        }

        @Override
        public String toString() {
            return "PopularBoard{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", viewCount=" + viewCount +
                    ", likeCount=" + likeCount +
                    ", commentCount=" + commentCount +
                    ", createdAt='" + createdAt + '\'' +
                    ", popularityScore=" + popularityScore +
                    '}';
        }
    }
}

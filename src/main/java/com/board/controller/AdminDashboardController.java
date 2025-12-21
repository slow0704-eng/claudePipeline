package com.board.controller;

import com.board.dto.DashboardStats;
import com.board.service.DashboardService;
import com.board.util.ApiResponse;
import com.board.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 관리자 대시보드 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    /**
     * 관리자 대시보드 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "admin/dashboard";
    }

    /**
     * 대시보드 통계 API (전체 데이터)
     *
     * 30초마다 자동 새로고침으로 호출됩니다.
     */
    @GetMapping("/dashboard/api/stats")
    @ResponseBody
    public ResponseEntity<ApiResponse> getDashboardStats() {
        try {
            DashboardStats stats = dashboardService.getDashboardStats();
            return ResponseBuilder.success(stats);
        } catch (Exception e) {
            return ResponseBuilder.error("통계 데이터를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 실시간 지표만 조회 API (빠른 업데이트용)
     */
    @GetMapping("/dashboard/api/realtime")
    @ResponseBody
    public ResponseEntity<ApiResponse> getRealTimeMetrics() {
        try {
            DashboardStats stats = dashboardService.getDashboardStats();
            return ResponseBuilder.success(stats.getRealTimeMetrics());
        } catch (Exception e) {
            return ResponseBuilder.error("실시간 지표를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 트렌드 데이터만 조회 API
     */
    @GetMapping("/dashboard/api/trends")
    @ResponseBody
    public ResponseEntity<ApiResponse> getTrendData() {
        try {
            DashboardStats stats = dashboardService.getDashboardStats();
            return ResponseBuilder.success(stats.getTrendData());
        } catch (Exception e) {
            return ResponseBuilder.error("트렌드 데이터를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 최근 활동만 조회 API
     */
    @GetMapping("/dashboard/api/activities")
    @ResponseBody
    public ResponseEntity<ApiResponse> getRecentActivity() {
        try {
            DashboardStats stats = dashboardService.getDashboardStats();
            return ResponseBuilder.success(stats.getRecentActivity());
        } catch (Exception e) {
            return ResponseBuilder.error("최근 활동을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 인기 게시글만 조회 API
     */
    @GetMapping("/dashboard/api/popular")
    @ResponseBody
    public ResponseEntity<ApiResponse> getPopularBoards() {
        try {
            DashboardStats stats = dashboardService.getDashboardStats();
            return ResponseBuilder.success(stats.getPopularBoards());
        } catch (Exception e) {
            return ResponseBuilder.error("인기 게시글을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

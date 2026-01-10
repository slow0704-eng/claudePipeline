package com.board.controller;

import com.board.dto.response.ApiResponse;
import com.board.entity.Community;
import com.board.entity.User;
import com.board.enums.CommunityType;
import com.board.service.CommunityService;
import com.board.service.CommunityStatisticsService;
import com.board.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 커뮤니티 탐색 컨트롤러
 * 커뮤니티 검색, 필터링, 정렬 기능 제공
 */
@Slf4j
@Controller
@RequestMapping("/community/explore")
@RequiredArgsConstructor
public class CommunityExploreController {

    private final CommunityService communityService;
    private final CommunityStatisticsService communityStatisticsService;

    /**
     * 커뮤니티 탐색 페이지 (템플릿)
     */
    @GetMapping
    public String explore(@RequestParam(required = false) String search,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false, defaultValue = "popular") String sort,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          @CurrentUser User currentUser,
                          Model model) {
        try {
            // 정렬 기준 설정
            Sort sorting = getSortCriteria(sort);
            Pageable pageable = PageRequest.of(page, size, sorting);

            Page<Community> communities;

            // 검색어가 있는 경우
            if (search != null && !search.trim().isEmpty()) {
                communities = communityService.searchCommunities(search, pageable);
            }
            // 타입 필터링
            else if (type != null && !type.isEmpty()) {
                CommunityType communityType = CommunityType.valueOf(type.toUpperCase());
                communities = communityService.getCommunitiesByType(communityType, pageable);
            }
            // 전체 목록
            else {
                communities = communityService.getActiveCommunities(pageable);
            }

            // 통계 정보 추가
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCommunities", communityService.getTotalActiveCommunityCount());
            stats.put("publicCount", communityService.getCountByType(CommunityType.PUBLIC));
            stats.put("privateCount", communityService.getCountByType(CommunityType.PRIVATE));

            model.addAttribute("communities", communities);
            model.addAttribute("stats", stats);
            model.addAttribute("searchKeyword", search);
            model.addAttribute("currentType", type);
            model.addAttribute("currentSort", sort);
            model.addAttribute("currentUser", currentUser);

            return "community/explore";
        } catch (Exception e) {
            log.error("커뮤니티 탐색 페이지 조회 실패", e);
            model.addAttribute("error", "커뮤니티 목록을 불러오는데 실패했습니다.");
            return "error";
        }
    }

    /**
     * 커뮤니티 통계 조회 (REST API)
     */
    @GetMapping("/api/statistics/{communityId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics(@PathVariable Long communityId) {
        try {
            Map<String, Object> stats = communityStatisticsService.getCommunityStatistics(communityId);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("커뮤니티 통계 조회 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 커뮤니티 요약 정보 조회 (REST API)
     */
    @GetMapping("/api/summary/{communityId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(@PathVariable Long communityId) {
        try {
            Map<String, Object> summary = communityStatisticsService.getCommunitySummary(communityId);
            return ResponseEntity.ok(ApiResponse.success(summary));
        } catch (Exception e) {
            log.error("커뮤니티 요약 정보 조회 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 커뮤니티 순위 정보 조회 (REST API)
     */
    @GetMapping("/api/ranking/{communityId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRanking(@PathVariable Long communityId) {
        try {
            Map<String, Object> ranking = communityStatisticsService.getCommunityRanking(communityId);
            return ResponseEntity.ok(ApiResponse.success(ranking));
        } catch (Exception e) {
            log.error("커뮤니티 순위 정보 조회 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 상위 활동 멤버 조회 (REST API)
     */
    @GetMapping("/api/top-members/{communityId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> getTopMembers(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            var topMembers = communityStatisticsService.getTopActiveMembers(communityId, limit);
            return ResponseEntity.ok(ApiResponse.success(topMembers));
        } catch (Exception e) {
            log.error("상위 활동 멤버 조회 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 멤버 성장 추이 조회 (REST API)
     */
    @GetMapping("/api/member-growth/{communityId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMemberGrowth(@PathVariable Long communityId) {
        try {
            Map<String, Long> growth = communityStatisticsService.getMemberGrowthTrend(communityId);
            return ResponseEntity.ok(ApiResponse.success(growth));
        } catch (Exception e) {
            log.error("멤버 성장 추이 조회 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 게시글 작성 추이 조회 (REST API)
     */
    @GetMapping("/api/board-trend/{communityId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBoardTrend(@PathVariable Long communityId) {
        try {
            Map<String, Long> trend = communityStatisticsService.getBoardCreationTrend(communityId);
            return ResponseEntity.ok(ApiResponse.success(trend));
        } catch (Exception e) {
            log.error("게시글 작성 추이 조회 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 정렬 기준 생성
     */
    private Sort getSortCriteria(String sort) {
        return switch (sort) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "members" -> Sort.by(Sort.Direction.DESC, "memberCount");
            case "activity" -> Sort.by(Sort.Direction.DESC, "boardCount");
            default -> Sort.by(Sort.Direction.DESC, "memberCount"); // popular (기본값)
        };
    }
}

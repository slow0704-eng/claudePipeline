package com.board.controller;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.service.SearchService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final UserService userService;

    /**
     * 검색 결과 페이지 (상세 필터링 포함)
     */
    @GetMapping("/search")
    public String search(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "type", defaultValue = "all") String searchType,
            @RequestParam(name = "sort", defaultValue = "latest") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            // 날짜 필터
            @RequestParam(name = "dateFrom", required = false) String dateFrom,
            @RequestParam(name = "dateTo", required = false) String dateTo,
            // 통계 필터
            @RequestParam(name = "minViews", required = false) Integer minViews,
            @RequestParam(name = "maxViews", required = false) Integer maxViews,
            @RequestParam(name = "minLikes", required = false) Integer minLikes,
            @RequestParam(name = "maxLikes", required = false) Integer maxLikes,
            @RequestParam(name = "minComments", required = false) Integer minComments,
            @RequestParam(name = "maxComments", required = false) Integer maxComments,
            Model model) {

        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("keyword", "");
            model.addAttribute("searchResults", Page.empty());
            model.addAttribute("totalResults", 0);
            return "board/search";
        }

        // 검색 수행 (필터 포함)
        Page<Board> searchResults = searchService.searchWithFilters(
                keyword, searchType, sortBy, page, size,
                dateFrom, dateTo,
                minViews, maxViews,
                minLikes, maxLikes,
                minComments, maxComments
        );

        // 검색 히스토리 저장 (로그인한 경우)
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser != null) {
            searchService.saveSearchHistory(currentUser.getId(), keyword);
        }

        // 모델에 검색 결과 및 필터 정보 추가
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("totalResults", searchResults.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());

        // 필터 값을 모델에 추가 (UI에서 유지하기 위해)
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("minViews", minViews);
        model.addAttribute("maxViews", maxViews);
        model.addAttribute("minLikes", minLikes);
        model.addAttribute("maxLikes", maxLikes);
        model.addAttribute("minComments", minComments);
        model.addAttribute("maxComments", maxComments);

        return "board/search";
    }

    /**
     * 검색어 자동완성 API
     */
    @GetMapping("/api/search/suggestions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSearchSuggestions(
            @RequestParam("q") String keyword) {

        List<String> suggestions = searchService.getSearchSuggestions(keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("suggestions", suggestions);
        return ResponseEntity.ok(response);
    }

    /**
     * 인기 검색어 API
     */
    @GetMapping("/api/search/popular")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPopularKeywords(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {

        List<String> popularKeywords = searchService.getPopularKeywords(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("keywords", popularKeywords);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 검색 히스토리 API
     */
    @GetMapping("/api/search/history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserSearchHistory() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        List<String> history = searchService.getUserSearchHistory(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("history", history);
        return ResponseEntity.ok(response);
    }

    /**
     * 검색 히스토리 삭제 API
     */
    @DeleteMapping("/api/search/history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearSearchHistory() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        searchService.clearUserSearchHistory(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "검색 히스토리가 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}

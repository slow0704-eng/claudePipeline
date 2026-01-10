package com.board.controller;

import com.board.dto.request.CategoryCreateRequest;
import com.board.dto.request.CategoryUpdateRequest;
import com.board.dto.request.CommunityBoardCreateRequest;
import com.board.dto.response.ApiResponse;
import com.board.entity.Board;
import com.board.entity.CommunityCategory;
import com.board.entity.User;
import com.board.enums.CommunityRole;
import com.board.service.BoardService;
import com.board.service.CommunityCategoryService;
import com.board.service.CommunityMemberService;
import com.board.service.CommunityService;
import com.board.util.CurrentUser;
import jakarta.validation.Valid;
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

import java.util.List;

/**
 * 커뮤니티 게시판 컨트롤러
 * 커뮤니티 내 게시글 작성, 조회 및 카테고리 관리
 */
@Slf4j
@Controller
@RequestMapping("/community/{communityId}/board")
@RequiredArgsConstructor
public class CommunityBoardController {

    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;
    private final CommunityCategoryService communityCategoryService;
    private final BoardService boardService;

    /**
     * 커뮤니티 게시판 목록 (템플릿)
     */
    @GetMapping
    public String list(@PathVariable Long communityId,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @CurrentUser User currentUser,
                       Model model) {
        try {
            // 커뮤니티 존재 및 접근 권한 확인
            communityService.getCommunityById(communityId);

            if (currentUser != null) {
                boolean canAccess = communityService.canAccessCommunity(communityId, currentUser.getId());
                if (!canAccess) {
                    model.addAttribute("error", "접근 권한이 없습니다.");
                    return "error";
                }

                // 사용자의 역할 정보
                CommunityRole userRole = communityService.getUserRole(communityId, currentUser.getId());
                model.addAttribute("userRole", userRole);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Board> boards;

            // 카테고리별 필터링
            if (categoryId != null) {
                boards = boardService.getCommunityBoardsByCategory(communityId, categoryId, pageable);
                model.addAttribute("selectedCategoryId", categoryId);
            } else {
                boards = boardService.getCommunityBoards(communityId, pageable);
            }

            // 카테고리 목록 조회
            List<CommunityCategory> categories = communityCategoryService.getEnabledCategoriesByCommunity(communityId);

            model.addAttribute("communityId", communityId);
            model.addAttribute("boards", boards);
            model.addAttribute("categories", categories);
            model.addAttribute("currentUser", currentUser);

            return "community/board-list";
        } catch (Exception e) {
            log.error("커뮤니티 게시판 목록 조회 실패: communityId={}", communityId, e);
            model.addAttribute("error", "게시판을 불러오는데 실패했습니다.");
            return "error";
        }
    }

    /**
     * 커뮤니티 게시글 작성 폼 (템플릿)
     */
    @GetMapping("/new")
    public String createForm(@PathVariable Long communityId,
                             @RequestParam(required = false) Long categoryId,
                             @CurrentUser User currentUser,
                             Model model) {
        try {
            // 커뮤니티 존재 확인
            communityService.getCommunityById(communityId);

            // 멤버 여부 확인
            if (currentUser == null || !communityMemberService.isMember(communityId, currentUser.getId())) {
                model.addAttribute("error", "커뮤니티 멤버만 게시글을 작성할 수 있습니다.");
                return "error";
            }

            // 카테고리 목록 조회
            List<CommunityCategory> categories = communityCategoryService.getEnabledCategoriesByCommunity(communityId);

            model.addAttribute("communityId", communityId);
            model.addAttribute("categories", categories);
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("currentUser", currentUser);

            return "community/board-form";
        } catch (Exception e) {
            log.error("커뮤니티 게시글 작성 폼 조회 실패: communityId={}", communityId, e);
            model.addAttribute("error", "게시글 작성 폼을 불러오는데 실패했습니다.");
            return "error";
        }
    }

    /**
     * 커뮤니티 게시글 작성
     */
    @PostMapping
    public String create(@PathVariable Long communityId,
                         @Valid @ModelAttribute CommunityBoardCreateRequest request,
                         @CurrentUser User currentUser,
                         Model model) {
        try {
            // 멤버 여부 확인
            if (currentUser == null || !communityMemberService.isMember(communityId, currentUser.getId())) {
                model.addAttribute("error", "커뮤니티 멤버만 게시글을 작성할 수 있습니다.");
                return "error";
            }

            // 게시글 생성
            Board board = request.toEntity(currentUser.getNickname(), currentUser.getNickname(), currentUser.getId());
            Board created = boardService.createCommunityBoard(board, communityId, request.getCommunityCategoryId());

            log.info("커뮤니티 게시글 작성 완료: communityId={}, boardId={}, userId={}",
                    communityId, created.getId(), currentUser.getId());

            return "redirect:/community/" + communityId + "/board";
        } catch (Exception e) {
            log.error("커뮤니티 게시글 작성 실패: communityId={}", communityId, e);
            model.addAttribute("error", "게시글 작성에 실패했습니다: " + e.getMessage());

            // 재시도를 위해 폼으로 다시 이동
            List<CommunityCategory> categories = communityCategoryService.getEnabledCategoriesByCommunity(communityId);
            model.addAttribute("communityId", communityId);
            model.addAttribute("categories", categories);
            model.addAttribute("request", request);
            return "community/board-form";
        }
    }

    // ==================== 카테고리 관리 REST API ====================

    /**
     * 카테고리 생성 (REST API)
     * OWNER/ADMIN만 가능
     */
    @PostMapping("/categories")
    @ResponseBody
    public ResponseEntity<ApiResponse<CommunityCategory>> createCategory(
            @PathVariable Long communityId,
            @Valid @RequestBody CategoryCreateRequest request,
            @CurrentUser User currentUser) {
        try {
            CommunityCategory category = communityCategoryService.createCategory(
                    communityId,
                    request.getName(),
                    request.getDescription(),
                    request.getDisplayOrder()
            );

            log.info("커뮤니티 카테고리 생성 성공: communityId={}, categoryId={}, userId={}",
                    communityId, category.getId(), currentUser.getId());

            return ResponseEntity.ok(ApiResponse.success(category));
        } catch (Exception e) {
            log.error("커뮤니티 카테고리 생성 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 카테고리 수정 (REST API)
     * OWNER/ADMIN만 가능
     */
    @PutMapping("/categories/{categoryId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<CommunityCategory>> updateCategory(
            @PathVariable Long communityId,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest request,
            @CurrentUser User currentUser) {
        try {
            CommunityCategory category = communityCategoryService.updateCategory(
                    categoryId,
                    request.getName(),
                    request.getDescription(),
                    request.getDisplayOrder()
            );

            log.info("커뮤니티 카테고리 수정 성공: communityId={}, categoryId={}, userId={}",
                    communityId, categoryId, currentUser.getId());

            return ResponseEntity.ok(ApiResponse.success(category));
        } catch (Exception e) {
            log.error("커뮤니티 카테고리 수정 실패: categoryId={}", categoryId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 카테고리 활성화/비활성화 (REST API)
     * OWNER/ADMIN만 가능
     */
    @PostMapping("/categories/{categoryId}/toggle")
    @ResponseBody
    public ResponseEntity<ApiResponse<CommunityCategory>> toggleCategory(
            @PathVariable Long communityId,
            @PathVariable Long categoryId,
            @CurrentUser User currentUser) {
        try {
            CommunityCategory category = communityCategoryService.toggleCategoryEnabled(categoryId);

            log.info("커뮤니티 카테고리 토글 성공: communityId={}, categoryId={}, enabled={}, userId={}",
                    communityId, categoryId, category.getEnabled(), currentUser.getId());

            return ResponseEntity.ok(ApiResponse.success(category));
        } catch (Exception e) {
            log.error("커뮤니티 카테고리 토글 실패: categoryId={}", categoryId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 카테고리 삭제 (REST API)
     * OWNER/ADMIN만 가능
     */
    @DeleteMapping("/categories/{categoryId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long communityId,
            @PathVariable Long categoryId,
            @CurrentUser User currentUser) {
        try {
            communityCategoryService.deleteCategory(categoryId);

            log.info("커뮤니티 카테고리 삭제 성공: communityId={}, categoryId={}, userId={}",
                    communityId, categoryId, currentUser.getId());

            return ResponseEntity.ok(ApiResponse.success("카테고리가 삭제되었습니다."));
        } catch (Exception e) {
            log.error("커뮤니티 카테고리 삭제 실패: categoryId={}", categoryId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 카테고리 목록 조회 (REST API)
     */
    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<CommunityCategory>>> getCategories(
            @PathVariable Long communityId) {
        try {
            List<CommunityCategory> categories = communityCategoryService.getCategoriesByCommunity(communityId);
            return ResponseEntity.ok(ApiResponse.success(categories));
        } catch (Exception e) {
            log.error("커뮤니티 카테고리 목록 조회 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 카테고리 순서 변경 (REST API)
     * OWNER/ADMIN만 가능
     */
    @PostMapping("/categories/reorder")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> reorderCategories(
            @PathVariable Long communityId,
            @RequestBody List<Long> categoryIds,
            @CurrentUser User currentUser) {
        try {
            communityCategoryService.reorderCategories(communityId, categoryIds);

            log.info("커뮤니티 카테고리 순서 변경 성공: communityId={}, userId={}",
                    communityId, currentUser.getId());

            return ResponseEntity.ok(ApiResponse.success("카테고리 순서가 변경되었습니다."));
        } catch (Exception e) {
            log.error("커뮤니티 카테고리 순서 변경 실패: communityId={}", communityId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

package com.board.controller;

import com.board.dto.response.ApiResponse;
import com.board.entity.Community;
import com.board.entity.CommunityMember;
import com.board.entity.User;
import com.board.enums.CommunityRole;
import com.board.enums.CommunityType;
import com.board.exception.BusinessException;
import com.board.service.CommunityMemberService;
import com.board.service.CommunityService;
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

import java.util.List;

/**
 * 커뮤니티 컨트롤러
 * 커뮤니티 목록, 상세, 생성, 수정, 삭제 및 멤버십 관리
 */
@Slf4j
@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;

    /**
     * 커뮤니티 목록 (템플릿)
     */
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) String search,
                       @CurrentUser User currentUser,
                       Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Community> communities;

            // 타입별 필터링
            if (type != null && !type.isEmpty()) {
                CommunityType communityType = CommunityType.valueOf(type.toUpperCase());
                communities = communityService.getCommunitiesByType(communityType, pageable);
            }
            // 검색
            else if (search != null && !search.isEmpty()) {
                communities = communityService.searchCommunities(search, pageable);
            }
            // 전체 목록
            else {
                communities = communityService.getActiveCommunities(pageable);
            }

            model.addAttribute("communities", communities);
            model.addAttribute("currentType", type);
            model.addAttribute("searchKeyword", search);
            model.addAttribute("currentUser", currentUser);

            return "community/list";
        } catch (Exception e) {
            log.error("커뮤니티 목록 조회 실패", e);
            model.addAttribute("error", "커뮤니티 목록을 불러오는데 실패했습니다.");
            return "error";
        }
    }

    /**
     * 커뮤니티 상세 (템플릿)
     */
    @GetMapping("/{id}")
    public String view(@PathVariable Long id,
                       @CurrentUser User currentUser,
                       Model model) {
        try {
            Community community = communityService.getCommunityById(id);

            // 접근 권한 확인 (PRIVATE, SECRET은 멤버만)
            if (currentUser != null) {
                boolean canAccess = communityService.canAccessCommunity(id, currentUser.getId());
                if (!canAccess) {
                    model.addAttribute("error", "접근 권한이 없습니다.");
                    return "error";
                }

                // 사용자의 역할 정보
                CommunityRole userRole = communityService.getUserRole(id, currentUser.getId());
                model.addAttribute("userRole", userRole);
                model.addAttribute("isMember", userRole != null);
            } else {
                // 비로그인 사용자는 PUBLIC만 접근 가능
                if (community.getType() != CommunityType.PUBLIC) {
                    model.addAttribute("error", "로그인이 필요합니다.");
                    return "redirect:/auth/login";
                }
            }

            // 멤버 목록 조회
            List<CommunityMember> members = communityMemberService.getCommunityMembers(id);

            model.addAttribute("community", community);
            model.addAttribute("members", members);
            model.addAttribute("currentUser", currentUser);

            return "community/view";
        } catch (BusinessException e) {
            log.error("커뮤니티 조회 실패: id={}", id, e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * 커뮤니티 생성 폼 (템플릿)
     */
    @GetMapping("/new")
    public String createForm(@CurrentUser User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("community", new Community());
        model.addAttribute("types", CommunityType.values());
        model.addAttribute("currentUser", currentUser);

        return "community/form";
    }

    /**
     * 커뮤니티 생성
     */
    @PostMapping
    public String create(@ModelAttribute Community community,
                         @CurrentUser User currentUser) {
        try {
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            Community created = communityService.createCommunity(community);
            log.info("커뮤니티 생성 완료: id={}, name={}, userId={}",
                    created.getId(), created.getName(), currentUser.getId());

            return "redirect:/community/" + created.getId();
        } catch (BusinessException e) {
            log.error("커뮤니티 생성 실패", e);
            return "redirect:/community/new?error=" + e.getMessage();
        }
    }

    /**
     * 커뮤니티 수정 폼 (템플릿)
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @CurrentUser User currentUser,
                           Model model) {
        try {
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            Community community = communityService.getCommunityById(id);

            // ADMIN 권한 확인
            if (!communityService.hasAdminPermission(id, currentUser.getId())) {
                model.addAttribute("error", "수정 권한이 없습니다.");
                return "error";
            }

            model.addAttribute("community", community);
            model.addAttribute("types", CommunityType.values());
            model.addAttribute("currentUser", currentUser);

            return "community/form";
        } catch (BusinessException e) {
            log.error("커뮤니티 수정 폼 조회 실패: id={}", id, e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * 커뮤니티 수정
     */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Community community,
                         @CurrentUser User currentUser) {
        try {
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            communityService.updateCommunity(id, community);
            log.info("커뮤니티 수정 완료: id={}, userId={}", id, currentUser.getId());

            return "redirect:/community/" + id;
        } catch (BusinessException e) {
            log.error("커뮤니티 수정 실패: id={}", id, e);
            return "redirect:/community/" + id + "/edit?error=" + e.getMessage();
        }
    }

    /**
     * 커뮤니티 삭제
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @CurrentUser User currentUser) {
        try {
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            communityService.deleteCommunity(id);
            log.info("커뮤니티 삭제 완료: id={}, userId={}", id, currentUser.getId());

            return "redirect:/community";
        } catch (BusinessException e) {
            log.error("커뮤니티 삭제 실패: id={}", id, e);
            return "redirect:/community/" + id + "?error=" + e.getMessage();
        }
    }

    // ==================== REST API 엔드포인트 ====================

    /**
     * 커뮤니티 가입 (REST API)
     */
    @PostMapping("/{id}/join")
    @ResponseBody
    public ResponseEntity<ApiResponse<CommunityMember>> join(@PathVariable Long id,
                                                               @CurrentUser User currentUser) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("로그인이 필요합니다."));
            }

            CommunityMember member = communityMemberService.joinCommunity(id);
            log.info("커뮤니티 가입 완료: communityId={}, userId={}", id, currentUser.getId());

            return ResponseEntity.ok(ApiResponse.success("커뮤니티에 가입했습니다.", member));
        } catch (BusinessException e) {
            log.error("커뮤니티 가입 실패: communityId={}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 커뮤니티 탈퇴 (REST API)
     */
    @PostMapping("/{id}/leave")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> leave(@PathVariable Long id,
                                                     @CurrentUser User currentUser) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("로그인이 필요합니다."));
            }

            communityMemberService.leaveCommunity(id);
            log.info("커뮤니티 탈퇴 완료: communityId={}, userId={}", id, currentUser.getId());

            return ResponseEntity.ok(ApiResponse.success("커뮤니티에서 탈퇴했습니다."));
        } catch (BusinessException e) {
            log.error("커뮤니티 탈퇴 실패: communityId={}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 멤버 역할 변경 (REST API)
     */
    @PostMapping("/{communityId}/members/{userId}/role")
    @ResponseBody
    public ResponseEntity<ApiResponse<CommunityMember>> changeRole(@PathVariable Long communityId,
                                                                     @PathVariable Long userId,
                                                                     @RequestParam CommunityRole role,
                                                                     @CurrentUser User currentUser) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("로그인이 필요합니다."));
            }

            CommunityMember member = communityMemberService.changeMemberRole(communityId, userId, role);
            log.info("멤버 역할 변경 완료: communityId={}, targetUserId={}, newRole={}, moderatorId={}",
                    communityId, userId, role, currentUser.getId());

            return ResponseEntity.ok(ApiResponse.success("멤버 역할을 변경했습니다.", member));
        } catch (BusinessException e) {
            log.error("멤버 역할 변경 실패: communityId={}, userId={}", communityId, userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 멤버 퇴출 (REST API)
     */
    @PostMapping("/{communityId}/members/{userId}/kick")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> kickMember(@PathVariable Long communityId,
                                                          @PathVariable Long userId,
                                                          @RequestParam(required = false) String reason,
                                                          @CurrentUser User currentUser) {
        try {
            if (currentUser == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("로그인이 필요합니다."));
            }

            communityMemberService.kickMember(communityId, userId, reason);
            log.info("멤버 퇴출 완료: communityId={}, targetUserId={}, moderatorId={}, reason={}",
                    communityId, userId, currentUser.getId(), reason);

            return ResponseEntity.ok(ApiResponse.success("멤버를 퇴출했습니다."));
        } catch (BusinessException e) {
            log.error("멤버 퇴출 실패: communityId={}, userId={}", communityId, userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 커뮤니티 멤버 목록 조회 (REST API)
     */
    @GetMapping("/{id}/members")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<CommunityMember>>> getMembers(@PathVariable Long id,
                                                                           @RequestParam(required = false) CommunityRole role) {
        try {
            List<CommunityMember> members;
            if (role != null) {
                members = communityMemberService.getMembersByRole(id, role);
            } else {
                members = communityMemberService.getCommunityMembers(id);
            }

            return ResponseEntity.ok(ApiResponse.success("멤버 목록 조회 완료", members));
        } catch (Exception e) {
            log.error("멤버 목록 조회 실패: communityId={}", id, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("멤버 목록 조회에 실패했습니다."));
        }
    }
}

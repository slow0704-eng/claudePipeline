package com.board.service;

import com.board.entity.Community;
import com.board.entity.CommunityCategory;
import com.board.entity.User;
import com.board.enums.CommunityRole;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.CommunityCategoryRepository;
import com.board.repository.CommunityRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 커뮤니티 카테고리 서비스
 * 카테고리 생성, 수정, 삭제, 조회 관리
 * OWNER/ADMIN만 관리 가능
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityCategoryService {

    private final CommunityCategoryRepository communityCategoryRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberService communityMemberService;
    private final UserService userService;

    /**
     * 카테고리 생성
     * OWNER/ADMIN만 가능
     */
    @Transactional
    public CommunityCategory createCategory(Long communityId, String name, String description, Integer displayOrder) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인 (OWNER/ADMIN만 가능)
        validateAdminPermission(communityId, currentUser.getId());

        // 커뮤니티 존재 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));

        // displayOrder가 null이면 마지막 순서로 설정
        if (displayOrder == null) {
            long count = communityCategoryRepository.countByCommunityId(communityId);
            displayOrder = (int) count;
        }

        // 카테고리 생성
        CommunityCategory category = new CommunityCategory();
        category.setCommunity(community);
        category.setCommunityId(communityId);
        category.setName(name);
        category.setDescription(description);
        category.setDisplayOrder(displayOrder);
        category.setEnabled(true);

        CommunityCategory saved = communityCategoryRepository.save(category);

        log.info("커뮤니티 카테고리 생성 완료: communityId={}, categoryId={}, name={}",
                communityId, saved.getId(), name);

        return saved;
    }

    /**
     * 카테고리 수정
     * OWNER/ADMIN만 가능
     */
    @Transactional
    public CommunityCategory updateCategory(Long categoryId, String name, String description, Integer displayOrder) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 카테고리 조회
        CommunityCategory category = communityCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_CATEGORY_NOT_FOUND));

        // 권한 확인
        validateAdminPermission(category.getCommunityId(), currentUser.getId());

        // 수정
        if (name != null) {
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }
        if (displayOrder != null) {
            category.setDisplayOrder(displayOrder);
        }

        CommunityCategory updated = communityCategoryRepository.save(category);

        log.info("커뮤니티 카테고리 수정 완료: categoryId={}, communityId={}",
                categoryId, category.getCommunityId());

        return updated;
    }

    /**
     * 카테고리 활성화/비활성화 토글
     * OWNER/ADMIN만 가능
     */
    @Transactional
    public CommunityCategory toggleCategoryEnabled(Long categoryId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 카테고리 조회
        CommunityCategory category = communityCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_CATEGORY_NOT_FOUND));

        // 권한 확인
        validateAdminPermission(category.getCommunityId(), currentUser.getId());

        // 토글
        category.setEnabled(!category.getEnabled());

        CommunityCategory updated = communityCategoryRepository.save(category);

        log.info("커뮤니티 카테고리 활성화 토글: categoryId={}, enabled={}",
                categoryId, updated.getEnabled());

        return updated;
    }

    /**
     * 카테고리 삭제
     * OWNER/ADMIN만 가능
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 카테고리 조회
        CommunityCategory category = communityCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_CATEGORY_NOT_FOUND));

        // 권한 확인
        validateAdminPermission(category.getCommunityId(), currentUser.getId());

        // 삭제
        communityCategoryRepository.delete(category);

        log.info("커뮤니티 카테고리 삭제 완료: categoryId={}, communityId={}",
                categoryId, category.getCommunityId());
    }

    /**
     * 커뮤니티의 모든 카테고리 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityCategory> getCategoriesByCommunity(Long communityId) {
        return communityCategoryRepository.findByCommunityIdOrderByDisplayOrderAsc(communityId);
    }

    /**
     * 커뮤니티의 활성화된 카테고리만 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityCategory> getEnabledCategoriesByCommunity(Long communityId) {
        return communityCategoryRepository.findByCommunityIdAndEnabledTrueOrderByDisplayOrderAsc(communityId);
    }

    /**
     * 카테고리 단건 조회
     */
    @Transactional(readOnly = true)
    public CommunityCategory getCategoryById(Long categoryId) {
        return communityCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_CATEGORY_NOT_FOUND));
    }

    /**
     * 카테고리 표시 순서 변경
     * OWNER/ADMIN만 가능
     */
    @Transactional
    public void reorderCategories(Long communityId, List<Long> categoryIds) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 권한 확인
        validateAdminPermission(communityId, currentUser.getId());

        // 각 카테고리의 순서 업데이트
        for (int i = 0; i < categoryIds.size(); i++) {
            Long categoryId = categoryIds.get(i);
            CommunityCategory category = communityCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_CATEGORY_NOT_FOUND));

            // 해당 커뮤니티의 카테고리인지 확인
            if (!category.getCommunityId().equals(communityId)) {
                throw new BusinessException(ErrorCode.INVALID_CATEGORY);
            }

            category.setDisplayOrder(i);
            communityCategoryRepository.save(category);
        }

        log.info("커뮤니티 카테고리 순서 변경 완료: communityId={}, count={}",
                communityId, categoryIds.size());
    }

    /**
     * OWNER/ADMIN 권한 검증
     */
    private void validateAdminPermission(Long communityId, Long userId) {
        CommunityRole role = communityMemberService.getMemberRole(communityId, userId);

        if (role != CommunityRole.OWNER && role != CommunityRole.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
}

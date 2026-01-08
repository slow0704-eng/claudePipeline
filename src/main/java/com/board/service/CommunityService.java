package com.board.service;

import com.board.entity.Community;
import com.board.entity.CommunityMember;
import com.board.entity.User;
import com.board.enums.CommunityRole;
import com.board.enums.CommunityType;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.CommunityMemberRepository;
import com.board.repository.CommunityRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 커뮤니티 서비스
 * 커뮤니티 생성, 조회, 수정, 삭제 및 권한 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserService userService;

    /**
     * 커뮤니티 생성
     * 생성자를 자동으로 OWNER로 추가
     */
    @Transactional
    public Community createCommunity(Community community) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // 커뮤니티 이름 중복 확인
        if (communityRepository.existsByName(community.getName())) {
            throw new BusinessException(ErrorCode.COMMUNITY_NAME_ALREADY_EXISTS);
        }

        // 커뮤니티 생성
        community.setOwner(currentUser);
        community.setOwnerId(currentUser.getId());
        community.setMemberCount(1);
        community.setBoardCount(0);
        community.setIsActive(true);

        Community savedCommunity = communityRepository.save(community);
        log.info("커뮤니티 생성 완료: id={}, name={}, ownerId={}",
                savedCommunity.getId(), savedCommunity.getName(), currentUser.getId());

        // 생성자를 OWNER로 자동 추가
        CommunityMember ownerMember = new CommunityMember();
        ownerMember.setCommunity(savedCommunity);
        ownerMember.setCommunityId(savedCommunity.getId());
        ownerMember.setUser(currentUser);
        ownerMember.setUserId(currentUser.getId());
        ownerMember.setRole(CommunityRole.OWNER);

        communityMemberRepository.save(ownerMember);
        log.info("커뮤니티 소유자 멤버십 생성 완료: communityId={}, userId={}, role=OWNER",
                savedCommunity.getId(), currentUser.getId());

        return savedCommunity;
    }

    /**
     * 커뮤니티 ID로 조회 (Fetch Join)
     */
    @Transactional(readOnly = true)
    public Community getCommunityById(Long id) {
        return communityRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));
    }

    /**
     * 커뮤니티 기본 정보 조회 (권한 체크 없음)
     */
    @Transactional(readOnly = true)
    public Community getCommunityByIdBasic(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMUNITY_NOT_FOUND));
    }

    /**
     * 활성화된 커뮤니티 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<Community> getActiveCommunities(Pageable pageable) {
        return communityRepository.findActiveCommunitiesWithOwner(pageable);
    }

    /**
     * 전체 커뮤니티 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public Page<Community> getAllCommunities(Pageable pageable) {
        return communityRepository.findAllWithOwner(pageable);
    }

    /**
     * 커뮤니티 타입별 조회
     */
    @Transactional(readOnly = true)
    public Page<Community> getCommunitiesByType(CommunityType type, Pageable pageable) {
        return communityRepository.findByTypeWithOwner(type, pageable);
    }

    /**
     * 커뮤니티 검색 (이름, 설명)
     */
    @Transactional(readOnly = true)
    public Page<Community> searchCommunities(String keyword, Pageable pageable) {
        return communityRepository.searchCommunitiesWithOwner(keyword, pageable);
    }

    /**
     * 사용자의 커뮤니티 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Community> getUserCommunities(Long userId) {
        return communityRepository.findByOwnerIdWithOwner(userId);
    }

    /**
     * 커뮤니티 수정
     * OWNER 또는 ADMIN만 가능
     */
    @Transactional
    public Community updateCommunity(Long id, Community updatedCommunity) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        Community community = getCommunityByIdBasic(id);

        // 권한 확인
        if (!hasAdminPermission(community.getId(), currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 이름 변경 시 중복 확인
        if (!community.getName().equals(updatedCommunity.getName())) {
            if (communityRepository.existsByName(updatedCommunity.getName())) {
                throw new BusinessException(ErrorCode.COMMUNITY_NAME_ALREADY_EXISTS);
            }
            community.setName(updatedCommunity.getName());
        }

        // 수정 가능한 필드 업데이트
        if (updatedCommunity.getDescription() != null) {
            community.setDescription(updatedCommunity.getDescription());
        }
        if (updatedCommunity.getType() != null) {
            community.setType(updatedCommunity.getType());
        }
        if (updatedCommunity.getProfileImageUrl() != null) {
            community.setProfileImageUrl(updatedCommunity.getProfileImageUrl());
        }
        if (updatedCommunity.getBannerImageUrl() != null) {
            community.setBannerImageUrl(updatedCommunity.getBannerImageUrl());
        }
        if (updatedCommunity.getRules() != null) {
            community.setRules(updatedCommunity.getRules());
        }

        Community saved = communityRepository.save(community);
        log.info("커뮤니티 수정 완료: id={}, userId={}", id, currentUser.getId());

        return saved;
    }

    /**
     * 커뮤니티 삭제
     * OWNER만 가능, cascade로 멤버/카테고리 자동 삭제
     */
    @Transactional
    public void deleteCommunity(Long id) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        Community community = getCommunityByIdBasic(id);

        // OWNER 권한 확인
        if (!isOwner(community.getId(), currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        communityRepository.delete(community);
        log.info("커뮤니티 삭제 완료: id={}, userId={}", id, currentUser.getId());
    }

    /**
     * 커뮤니티 활성화/비활성화 (관리자 전용)
     */
    @Transactional
    public Community toggleCommunityActive(Long id) {
        Community community = getCommunityByIdBasic(id);
        community.setIsActive(!community.getIsActive());

        Community saved = communityRepository.save(community);
        log.info("커뮤니티 활성화 상태 변경: id={}, isActive={}", id, saved.getIsActive());

        return saved;
    }

    /**
     * 커뮤니티 멤버 수 증가
     */
    @Transactional
    public void increaseMemberCount(Long communityId) {
        Community community = getCommunityByIdBasic(communityId);
        community.increaseMemberCount();
        communityRepository.save(community);
        log.debug("커뮤니티 멤버 수 증가: communityId={}, memberCount={}",
                communityId, community.getMemberCount());
    }

    /**
     * 커뮤니티 멤버 수 감소
     */
    @Transactional
    public void decreaseMemberCount(Long communityId) {
        Community community = getCommunityByIdBasic(communityId);
        community.decreaseMemberCount();
        communityRepository.save(community);
        log.debug("커뮤니티 멤버 수 감소: communityId={}, memberCount={}",
                communityId, community.getMemberCount());
    }

    /**
     * 커뮤니티 게시글 수 증가
     */
    @Transactional
    public void increaseBoardCount(Long communityId) {
        Community community = getCommunityByIdBasic(communityId);
        community.increaseBoardCount();
        communityRepository.save(community);
        log.debug("커뮤니티 게시글 수 증가: communityId={}, boardCount={}",
                communityId, community.getBoardCount());
    }

    /**
     * 커뮤니티 게시글 수 감소
     */
    @Transactional
    public void decreaseBoardCount(Long communityId) {
        Community community = getCommunityByIdBasic(communityId);
        community.decreaseBoardCount();
        communityRepository.save(community);
        log.debug("커뮤니티 게시글 수 감소: communityId={}, boardCount={}",
                communityId, community.getBoardCount());
    }

    // ==================== 권한 확인 메서드 ====================

    /**
     * 사용자가 커뮤니티의 OWNER인지 확인
     */
    @Transactional(readOnly = true)
    public boolean isOwner(Long communityId, Long userId) {
        return communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .map(member -> member.getRole() == CommunityRole.OWNER)
                .orElse(false);
    }

    /**
     * 사용자가 커뮤니티의 ADMIN 권한(OWNER 또는 ADMIN)을 가지는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasAdminPermission(Long communityId, Long userId) {
        return communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .map(member -> member.getRole() == CommunityRole.OWNER ||
                              member.getRole() == CommunityRole.ADMIN)
                .orElse(false);
    }

    /**
     * 사용자가 커뮤니티의 멤버인지 확인
     */
    @Transactional(readOnly = true)
    public boolean isMember(Long communityId, Long userId) {
        return communityMemberRepository.existsByCommunityIdAndUserId(communityId, userId);
    }

    /**
     * 사용자의 커뮤니티 역할 조회
     */
    @Transactional(readOnly = true)
    public CommunityRole getUserRole(Long communityId, Long userId) {
        return communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .map(CommunityMember::getRole)
                .orElse(null);
    }

    /**
     * 커뮤니티 접근 권한 확인
     * PUBLIC: 모든 사용자
     * PRIVATE: 멤버만
     * SECRET: 멤버만
     */
    @Transactional(readOnly = true)
    public boolean canAccessCommunity(Long communityId, Long userId) {
        Community community = getCommunityByIdBasic(communityId);

        // PUBLIC 커뮤니티는 모든 사용자 접근 가능
        if (community.getType() == CommunityType.PUBLIC) {
            return true;
        }

        // PRIVATE, SECRET 커뮤니티는 멤버만 접근 가능
        return isMember(communityId, userId);
    }
}

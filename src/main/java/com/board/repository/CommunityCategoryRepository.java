package com.board.repository;

import com.board.entity.CommunityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커뮤니티 카테고리 Repository
 */
@Repository
public interface CommunityCategoryRepository extends JpaRepository<CommunityCategory, Long> {

    /**
     * 커뮤니티의 모든 카테고리 조회 (displayOrder로 정렬)
     */
    List<CommunityCategory> findByCommunityIdOrderByDisplayOrderAsc(Long communityId);

    /**
     * 커뮤니티의 활성화된 카테고리 조회
     */
    List<CommunityCategory> findByCommunityIdAndEnabledTrueOrderByDisplayOrderAsc(Long communityId);

    /**
     * 커뮤니티별 카테고리 수
     */
    long countByCommunityId(Long communityId);
}

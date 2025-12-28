package com.board.repository;

import com.board.entity.UserHashtagFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserHashtagFollowRepository extends JpaRepository<UserHashtagFollow, Long> {

    /**
     * 사용자가 특정 해시태그를 팔로우하는지 확인
     */
    boolean existsByUserIdAndHashtagId(Long userId, Long hashtagId);

    /**
     * 사용자-해시태그 팔로우 관계 찾기
     */
    Optional<UserHashtagFollow> findByUserIdAndHashtagId(Long userId, Long hashtagId);

    /**
     * 사용자가 팔로우한 모든 해시태그 ID 목록
     */
    @Query("SELECT uhf.hashtagId FROM UserHashtagFollow uhf WHERE uhf.userId = :userId ORDER BY uhf.followedAt DESC")
    List<Long> findHashtagIdsByUserId(@Param("userId") Long userId);

    /**
     * 특정 해시태그를 팔로우하는 사용자 수
     */
    long countByHashtagId(Long hashtagId);

    /**
     * 사용자가 팔로우한 해시태그 수
     */
    long countByUserId(Long userId);

    /**
     * 사용자가 팔로우한 모든 해시태그 관계 목록
     */
    List<UserHashtagFollow> findByUserIdOrderByFollowedAtDesc(Long userId);

    /**
     * 특정 해시태그를 팔로우하는 모든 사용자 ID
     */
    @Query("SELECT uhf.userId FROM UserHashtagFollow uhf WHERE uhf.hashtagId = :hashtagId")
    List<Long> findUserIdsByHashtagId(@Param("hashtagId") Long hashtagId);
}

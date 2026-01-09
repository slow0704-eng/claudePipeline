package com.board.repository;

import com.board.entity.UserTopicFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopicFollowRepository extends JpaRepository<UserTopicFollow, Long> {

    /**
     * 팔로우 여부 확인
     */
    boolean existsByUserIdAndTopicId(Long userId, Long topicId);

    /**
     * 팔로우 관계 조회
     */
    Optional<UserTopicFollow> findByUserIdAndTopicId(Long userId, Long topicId);

    /**
     * 사용자가 팔로우한 토픽 ID 목록 (최신순)
     */
    @Query("SELECT utf.topicId FROM UserTopicFollow utf WHERE utf.userId = :userId ORDER BY utf.followedAt DESC")
    List<Long> findTopicIdsByUserId(@Param("userId") Long userId);

    /**
     * 토픽 팔로워 수
     */
    long countByTopicId(Long topicId);

    /**
     * 사용자 팔로우 수
     */
    long countByUserId(Long userId);

    /**
     * 팔로우 목록 조회 (최신순)
     */
    List<UserTopicFollow> findByUserIdOrderByFollowedAtDesc(Long userId);

    /**
     * 토픽 팔로워 ID 목록
     */
    @Query("SELECT utf.userId FROM UserTopicFollow utf WHERE utf.topicId = :topicId")
    List<Long> findUserIdsByTopicId(@Param("topicId") Long topicId);

    /**
     * 사용자의 팔로우 삭제
     */
    void deleteByUserIdAndTopicId(Long userId, Long topicId);
}

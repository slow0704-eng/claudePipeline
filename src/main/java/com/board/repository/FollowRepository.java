package com.board.repository;

import com.board.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Check if user A follows user B
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Find follow relationship
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Get all users that userId is following
    List<Follow> findByFollowerId(Long followerId);

    // Get all users that are following userId
    List<Follow> findByFollowingId(Long followingId);

    // Count followers
    long countByFollowingId(Long followingId);

    // Count following
    long countByFollowerId(Long followerId);

    // Delete follow relationship
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Get all user IDs that userId is following
    @Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :followerId")
    List<Long> findFollowingUserIds(@Param("followerId") Long followerId);
}

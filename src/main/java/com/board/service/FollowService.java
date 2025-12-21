package com.board.service;

import com.board.entity.Follow;
import com.board.entity.User;
import com.board.repository.FollowRepository;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> toggleFollow(Long followerId, Long followingId) {
        // Check if trying to follow self
        if (followerId.equals(followingId)) {
            throw new RuntimeException("자기 자신을 팔로우할 수 없습니다.");
        }

        // Check if following user exists
        User followingUser = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        boolean isFollowing;
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            // Unfollow
            followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            isFollowing = false;
        } else {
            // Follow
            Follow follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            followRepository.save(follow);
            isFollowing = true;
        }

        long followerCount = followRepository.countByFollowingId(followingId);

        Map<String, Object> result = new HashMap<>();
        result.put("isFollowing", isFollowing);
        result.put("followerCount", followerCount);
        return result;
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            return false;
        }
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public long getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    public List<User> getFollowers(Long userId) {
        List<Follow> follows = followRepository.findByFollowingId(userId);
        List<Long> followerIds = follows.stream()
                .map(Follow::getFollowerId)
                .collect(Collectors.toList());

        return userRepository.findAllById(followerIds);
    }

    public List<User> getFollowing(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        List<Long> followingIds = follows.stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toList());

        return userRepository.findAllById(followingIds);
    }
}

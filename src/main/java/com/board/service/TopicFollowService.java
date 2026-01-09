package com.board.service;

import com.board.entity.Topic;
import com.board.entity.UserTopicFollow;
import com.board.repository.TopicRepository;
import com.board.repository.UserTopicFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicFollowService {

    private final UserTopicFollowRepository userTopicFollowRepository;
    private final TopicRepository topicRepository;

    /**
     * 토픽 팔로우/언팔로우 토글
     */
    @Transactional
    public Map<String, Object> toggleTopicFollow(Long userId, Long topicId) {
        if (userId == null || topicId == null) {
            throw new RuntimeException("사용자 ID와 토픽 ID는 필수입니다.");
        }

        // 토픽 존재 여부 확인
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토픽입니다."));

        if (!topic.getEnabled()) {
            throw new RuntimeException("비활성화된 토픽입니다.");
        }

        Optional<UserTopicFollow> existing =
                userTopicFollowRepository.findByUserIdAndTopicId(userId, topicId);

        boolean isFollowing;
        if (existing.isPresent()) {
            // 언팔로우
            userTopicFollowRepository.delete(existing.get());
            isFollowing = false;
        } else {
            // 팔로우
            UserTopicFollow follow = new UserTopicFollow();
            follow.setUserId(userId);
            follow.setTopicId(topicId);
            userTopicFollowRepository.save(follow);
            isFollowing = true;
        }

        long followerCount = userTopicFollowRepository.countByTopicId(topicId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("isFollowing", isFollowing);
        result.put("followerCount", followerCount);
        result.put("topicId", topicId);
        result.put("topicName", topic.getName());

        return result;
    }

    /**
     * 팔로우 여부 확인
     */
    public boolean isFollowingTopic(Long userId, Long topicId) {
        if (userId == null || topicId == null) {
            return false;
        }
        return userTopicFollowRepository.existsByUserIdAndTopicId(userId, topicId);
    }

    /**
     * 팔로우한 토픽 목록 조회
     */
    public List<Topic> getFollowedTopics(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<Long> topicIds = userTopicFollowRepository.findTopicIdsByUserId(userId);

        return topicIds.stream()
                .map(id -> topicRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(Topic::getEnabled) // 활성화된 토픽만
                .collect(Collectors.toList());
    }

    /**
     * 팔로우한 토픽 상세 정보 조회 (팔로워 수 포함)
     */
    public List<Map<String, Object>> getFollowedTopicsWithDetails(Long userId) {
        List<Topic> topics = getFollowedTopics(userId);

        return topics.stream()
                .map(topic -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", topic.getId());
                    map.put("name", topic.getName());
                    map.put("description", topic.getDescription());
                    map.put("icon", topic.getIcon());
                    map.put("color", topic.getColor());
                    map.put("level", topic.getLevel());
                    map.put("usageCount", topic.getUsageCount());
                    map.put("followerCount", getFollowerCount(topic.getId()));
                    map.put("lastUsedAt", topic.getLastUsedAt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 토픽 팔로워 수 조회
     */
    public long getFollowerCount(Long topicId) {
        return userTopicFollowRepository.countByTopicId(topicId);
    }

    /**
     * 사용자의 토픽 팔로우 수 조회
     */
    public long getUserFollowCount(Long userId) {
        return userTopicFollowRepository.countByUserId(userId);
    }

    /**
     * 토픽의 팔로워 ID 목록 조회
     */
    public List<Long> getFollowerIds(Long topicId) {
        return userTopicFollowRepository.findUserIdsByTopicId(topicId);
    }
}

package com.board.service;

import com.board.entity.Topic;
import com.board.repository.TopicRepository;
import com.board.repository.UserTopicFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 토픽 탐색 서비스
 * - 인기 토픽, 새로운 토픽, 트렌딩 토픽 조회
 * - 토픽 발견 및 탐색 기능
 * - Phase 6: 통계 캐시 활용으로 성능 최적화
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicExploreService {

    private final TopicRepository topicRepository;
    private final UserTopicFollowRepository userTopicFollowRepository;
    private final TopicStatsCacheService statsCacheService;

    /**
     * 인기 토픽 조회 (usageCount 기준)
     * Phase 6: 캐시 우선 조회로 성능 최적화
     *
     * @param limit 조회할 토픽 수
     * @return 인기 토픽 목록
     */
    public List<Map<String, Object>> getPopularTopics(int limit) {
        // 캐시 조회 시도
        try {
            var caches = statsCacheService.getPopularTopicsCache(limit);
            if (!caches.isEmpty()) {
                return caches.stream()
                    .map(cache -> {
                        Topic topic = topicRepository.findById(cache.getTopicId()).orElse(null);
                        if (topic == null) return null;

                        Map<String, Object> map = topicToMap(topic);
                        map.put("postCount", cache.getPostCount());
                        map.put("followerCount", cache.getFollowerCount());
                        map.put("popularityRank", cache.getPopularityRank());
                        return map;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // 캐시 조회 실패 시 실시간 조회로 폴백
        }

        // 폴백: 실시간 조회
        List<Topic> topics = topicRepository.findTopPopularTopics(PageRequest.of(0, limit));
        return topics.stream()
            .map(this::topicToMap)
            .collect(Collectors.toList());
    }

    /**
     * 새로운 토픽 조회 (최근 30일)
     *
     * @param limit 조회할 토픽 수
     * @return 새로운 토픽 목록
     */
    public List<Map<String, Object>> getNewTopics(int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Topic> topics = topicRepository.findNewTopics(since);

        return topics.stream()
            .limit(limit)
            .map(this::topicToMap)
            .collect(Collectors.toList());
    }

    /**
     * 트렌딩/성장세 토픽 조회 (최근 7일 vs 이전 7일)
     * Phase 6: 캐시 우선 조회로 성능 최적화
     *
     * @param limit 조회할 토픽 수
     * @return 성장세 토픽 목록
     */
    public List<Map<String, Object>> getTrendingTopics(int limit) {
        // 캐시 조회 시도
        try {
            var caches = statsCacheService.getGrowingTopicsCache(limit);
            if (!caches.isEmpty()) {
                return caches.stream()
                    .map(cache -> {
                        Topic topic = topicRepository.findById(cache.getTopicId()).orElse(null);
                        if (topic == null) return null;

                        Map<String, Object> map = new HashMap<>();
                        map.put("id", topic.getId());
                        map.put("name", topic.getName());
                        map.put("icon", topic.getIcon());
                        map.put("color", topic.getColor());
                        map.put("recentPostCount", cache.getPosts7days());
                        map.put("previousPostCount", 0); // 이전 7일 데이터는 캐시에 없음
                        map.put("growthRate", cache.getGrowthRate7days().doubleValue());
                        map.put("followerCount", cache.getFollowerCount());
                        map.put("growthRank", cache.getGrowthRank());
                        return map;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // 캐시 조회 실패 시 실시간 조회로 폴백
        }

        // 폴백: 실시간 조회
        LocalDateTime recentStart = LocalDateTime.now().minusDays(7);
        LocalDateTime oldStart = LocalDateTime.now().minusDays(14);

        List<Object[]> results = topicRepository.findGrowingTopics(recentStart, oldStart, limit);

        return results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", ((Number) row[0]).longValue());
                map.put("name", row[1]);
                map.put("icon", row[2]);
                map.put("color", row[3]);
                map.put("recentPostCount", ((Number) row[4]).longValue());
                map.put("previousPostCount", ((Number) row[5]).longValue());
                map.put("growthRate", ((Number) row[6]).doubleValue());

                // 팔로워 수 추가
                long followCount = userTopicFollowRepository.countByTopicId(((Number) row[0]).longValue());
                map.put("followerCount", followCount);

                return map;
            })
            .collect(Collectors.toList());
    }

    /**
     * 팔로워 수 기준 인기 토픽
     * Phase 6: 캐시 우선 조회로 성능 최적화
     *
     * @param limit 조회할 토픽 수
     * @return 팔로워가 많은 토픽 목록
     */
    public List<Map<String, Object>> getTopicsByFollowerCount(int limit) {
        // 캐시 조회 시도
        try {
            var caches = statsCacheService.getMostFollowedTopicsCache(limit);
            if (!caches.isEmpty()) {
                return caches.stream()
                    .map(cache -> {
                        Topic topic = topicRepository.findById(cache.getTopicId()).orElse(null);
                        if (topic == null) return null;

                        Map<String, Object> map = topicToMap(topic);
                        map.put("followerCount", cache.getFollowerCount());
                        map.put("followerRank", cache.getFollowerRank());
                        return map;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // 캐시 조회 실패 시 실시간 조회로 폴백
        }

        // 폴백: 실시간 조회
        List<Object[]> results = topicRepository.findTopicsByFollowerCount();

        return results.stream()
            .limit(limit)
            .map(row -> {
                Long topicId = ((Number) row[0]).longValue();
                Topic topic = topicRepository.findById(topicId).orElse(null);
                if (topic == null) return null;

                Map<String, Object> map = topicToMap(topic);
                map.put("followerCount", ((Number) row[2]).longValue());
                return map;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 최근 활발한 토픽 (최근 7일 게시글 수 기준)
     *
     * @param limit 조회할 토픽 수
     * @return 최근 활발한 토픽 목록
     */
    public List<Map<String, Object>> getActiveTopics(int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Object[]> results = topicRepository.findActiveTopicsInPeriod(since, limit);

        return results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", ((Number) row[0]).longValue());
                map.put("name", row[1]);
                map.put("icon", row[2]);
                map.put("color", row[3]);
                map.put("recentPostCount", ((Number) row[4]).longValue());

                // 팔로워 수 추가
                long followCount = userTopicFollowRepository.countByTopicId(((Number) row[0]).longValue());
                map.put("followerCount", followCount);

                return map;
            })
            .collect(Collectors.toList());
    }

    /**
     * 토픽 탐색 통합 데이터 (한 번에 여러 카테고리 조회)
     *
     * @param userId 사용자 ID (팔로우 상태 확인용, null 가능)
     * @return 탐색 데이터 맵
     */
    public Map<String, Object> getExploreData(Long userId) {
        Map<String, Object> exploreData = new HashMap<>();

        // 각 카테고리별 토픽 조회
        exploreData.put("popular", getPopularTopics(10));
        exploreData.put("new", getNewTopics(10));
        exploreData.put("trending", getTrendingTopics(10));
        exploreData.put("mostFollowed", getTopicsByFollowerCount(10));
        exploreData.put("active", getActiveTopics(10));

        // 사용자가 팔로우한 토픽 ID 목록
        if (userId != null) {
            List<Long> followedTopicIds = userTopicFollowRepository.findTopicIdsByUserId(userId);
            exploreData.put("followedTopicIds", followedTopicIds);
        }

        return exploreData;
    }

    /**
     * 특정 카테고리의 토픽 조회 (API용)
     *
     * @param category 카테고리 (popular, new, trending, mostFollowed, active)
     * @param limit 조회할 토픽 수
     * @return 토픽 목록
     */
    public List<Map<String, Object>> getTopicsByCategory(String category, int limit) {
        switch (category.toLowerCase()) {
            case "popular":
                return getPopularTopics(limit);
            case "new":
                return getNewTopics(limit);
            case "trending":
                return getTrendingTopics(limit);
            case "mostfollowed":
            case "most-followed":
                return getTopicsByFollowerCount(limit);
            case "active":
                return getActiveTopics(limit);
            default:
                throw new IllegalArgumentException("지원하지 않는 카테고리입니다: " + category);
        }
    }

    /**
     * Topic 엔티티를 Map으로 변환
     */
    private Map<String, Object> topicToMap(Topic topic) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", topic.getId());
        map.put("name", topic.getName());
        map.put("description", topic.getDescription());
        map.put("icon", topic.getIcon());
        map.put("color", topic.getColor());
        map.put("level", topic.getLevel());
        map.put("usageCount", topic.getUsageCount());
        map.put("createdAt", topic.getCreatedAt());
        map.put("lastUsedAt", topic.getLastUsedAt());

        // 팔로워 수 추가
        long followerCount = userTopicFollowRepository.countByTopicId(topic.getId());
        map.put("followerCount", followerCount);

        return map;
    }

    /**
     * 토픽 검색 (탐색 페이지용)
     *
     * @param query 검색 쿼리
     * @param limit 조회할 토픽 수
     * @return 검색된 토픽 목록
     */
    public List<Map<String, Object>> searchTopicsForExplore(String query, int limit) {
        List<Topic> topics = topicRepository.searchTopics(query);

        return topics.stream()
            .limit(limit)
            .map(this::topicToMap)
            .collect(Collectors.toList());
    }

    /**
     * 토픽 상세 정보 조회 (탐색용)
     *
     * @param topicId 토픽 ID
     * @return 토픽 상세 정보
     */
    public Map<String, Object> getTopicDetailForExplore(Long topicId) {
        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null || !topic.getEnabled()) {
            return Collections.emptyMap();
        }

        Map<String, Object> detail = topicToMap(topic);

        // 추가 통계 정보
        long followerCount = userTopicFollowRepository.countByTopicId(topicId);
        detail.put("followerCount", followerCount);

        // 최근 7일 게시글 수
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Object[]> recentActivity = topicRepository.findActiveTopicsInPeriod(since, 1);
        if (!recentActivity.isEmpty()) {
            Object[] row = recentActivity.get(0);
            if (((Number) row[0]).longValue() == topicId) {
                detail.put("recentPostCount", ((Number) row[4]).longValue());
            }
        }

        return detail;
    }
}

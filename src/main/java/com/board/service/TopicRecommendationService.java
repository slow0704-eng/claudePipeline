package com.board.service;

import com.board.entity.Topic;
import com.board.repository.TopicActivityLogRepository;
import com.board.repository.TopicRepository;
import com.board.repository.UserTopicFollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 토픽 추천 서비스
 * - 협업 필터링 + 컨텐츠 기반 추천
 * - 개인화된 토픽 추천
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TopicRecommendationService {

    private final UserTopicFollowRepository userTopicFollowRepository;
    private final TopicActivityLogRepository activityLogRepository;
    private final TopicRepository topicRepository;

    // 추천 파라미터
    private static final int MAX_SIMILAR_USERS = 50; // 최대 유사 사용자 수
    private static final int ACTIVITY_DAYS = 90; // 활동 로그 조회 기간
    private static final double COLLABORATIVE_WEIGHT = 0.6; // 협업 필터링 가중치
    private static final double CONTENT_WEIGHT = 0.4; // 컨텐츠 기반 가중치

    /**
     * 하이브리드 추천 (협업 필터링 + 컨텐츠 기반)
     *
     * @param userId 사용자 ID
     * @param limit 추천 토픽 수
     * @return 추천 토픽 목록
     */
    public List<Map<String, Object>> getRecommendedTopics(Long userId, int limit) {
        // 1. 협업 필터링 추천
        List<Map<String, Object>> collaborativeResults = getCollaborativeRecommendations(userId, limit * 2);

        // 2. 컨텐츠 기반 추천
        List<Map<String, Object>> contentResults = getContentBasedRecommendations(userId, limit * 2);

        // 3. 두 결과 병합 (가중치 적용)
        Map<Long, Double> combinedScores = new HashMap<>();

        // 협업 필터링 점수 추가
        for (Map<String, Object> result : collaborativeResults) {
            Long topicId = (Long) result.get("topicId");
            Double score = (Double) result.get("score");
            combinedScores.put(topicId, score * COLLABORATIVE_WEIGHT);
        }

        // 컨텐츠 기반 점수 추가
        for (Map<String, Object> result : contentResults) {
            Long topicId = (Long) result.get("topicId");
            Double score = (Double) result.get("score");
            combinedScores.merge(topicId, score * CONTENT_WEIGHT, Double::sum);
        }

        // 4. 사용자가 이미 팔로우한 토픽 제외
        List<Long> followedTopicIds = userTopicFollowRepository.findTopicIdsByUserId(userId);
        combinedScores.keySet().removeAll(followedTopicIds);

        // 5. 점수 기준 정렬 및 상위 N개 선택
        List<Map<String, Object>> recommendations = combinedScores.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(limit)
            .map(entry -> {
                Long topicId = entry.getKey();
                Double score = entry.getValue();

                Topic topic = topicRepository.findById(topicId).orElse(null);
                if (topic == null || !topic.getEnabled()) {
                    return null;
                }

                Map<String, Object> map = new HashMap<>();
                map.put("topicId", topicId);
                map.put("topicName", topic.getName());
                map.put("topicIcon", topic.getIcon());
                map.put("topicColor", topic.getColor());
                map.put("score", score);
                map.put("usageCount", topic.getUsageCount());
                map.put("followerCount", userTopicFollowRepository.countByTopicId(topicId));
                map.put("reason", "추천");

                return map;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return recommendations;
    }

    /**
     * 협업 필터링 추천
     * - 비슷한 사용자들이 팔로우한 토픽 추천
     */
    private List<Map<String, Object>> getCollaborativeRecommendations(Long userId, int limit) {
        // 1. 사용자가 팔로우한 토픽 목록
        List<Long> userFollowedTopics = userTopicFollowRepository.findTopicIdsByUserId(userId);

        if (userFollowedTopics.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 유사 사용자 찾기 (같은 토픽을 팔로우한 사용자들)
        Map<Long, Set<Long>> userTopicMap = new HashMap<>();

        for (Long topicId : userFollowedTopics) {
            List<Long> followerIds = userTopicFollowRepository.findUserIdsByTopicId(topicId);
            for (Long followerId : followerIds) {
                if (!followerId.equals(userId)) {
                    userTopicMap.computeIfAbsent(followerId, k -> new HashSet<>()).add(topicId);
                }
            }
        }

        // 3. Jaccard Similarity 계산
        List<Map.Entry<Long, Double>> similarUsers = userTopicMap.entrySet().stream()
            .map(entry -> {
                Long otherUserId = entry.getKey();
                Set<Long> otherUserTopics = entry.getValue();

                double similarity = calculateJaccardSimilarity(
                    new HashSet<>(userFollowedTopics),
                    otherUserTopics
                );

                return Map.entry(otherUserId, similarity);
            })
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(MAX_SIMILAR_USERS)
            .collect(Collectors.toList());

        // 4. 유사 사용자들이 팔로우한 토픽 추천
        Map<Long, Double> topicScores = new HashMap<>();

        for (Map.Entry<Long, Double> entry : similarUsers) {
            Long similarUserId = entry.getKey();
            Double similarity = entry.getValue();

            List<Long> similarUserTopics = userTopicFollowRepository.findTopicIdsByUserId(similarUserId);

            for (Long topicId : similarUserTopics) {
                if (!userFollowedTopics.contains(topicId)) {
                    topicScores.merge(topicId, similarity, Double::sum);
                }
            }
        }

        // 5. 점수 기준 정렬
        return topicScores.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(limit)
            .map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("topicId", entry.getKey());
                map.put("score", entry.getValue());
                return map;
            })
            .collect(Collectors.toList());
    }

    /**
     * 컨텐츠 기반 추천
     * - 사용자가 활동한 토픽의 관련 토픽 추천
     */
    private List<Map<String, Object>> getContentBasedRecommendations(Long userId, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(ACTIVITY_DAYS);

        // 1. 사용자의 활동 점수 집계
        List<Object[]> activityScores = activityLogRepository.findUserTopicActivityScores(userId, since);

        if (activityScores.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 활동한 토픽의 관련 토픽 찾기
        Map<Long, Double> relatedTopicScores = new HashMap<>();

        for (Object[] row : activityScores) {
            Long topicId = ((Number) row[0]).longValue();
            BigDecimal activityScore = (BigDecimal) row[1];

            // 관련 토픽 조회 (co-occurrence)
            List<Object[]> relatedTopics = activityLogRepository.findRelatedTopicsByCooccurrence(
                topicId, since, 20
            );

            for (Object[] relatedRow : relatedTopics) {
                Long relatedTopicId = ((Number) relatedRow[0]).longValue();
                Long userCount = ((Number) relatedRow[1]).longValue();

                // 점수 = 활동 점수 * 관련도 (공통 사용자 수)
                double score = activityScore.doubleValue() * userCount;
                relatedTopicScores.merge(relatedTopicId, score, Double::sum);
            }
        }

        // 3. 사용자가 이미 활동한 토픽 제외
        List<Long> activeTopics = activityScores.stream()
            .map(row -> ((Number) row[0]).longValue())
            .collect(Collectors.toList());
        relatedTopicScores.keySet().removeAll(activeTopics);

        // 4. 점수 기준 정렬
        return relatedTopicScores.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(limit)
            .map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("topicId", entry.getKey());
                map.put("score", entry.getValue());
                return map;
            })
            .collect(Collectors.toList());
    }

    /**
     * Jaccard Similarity 계산
     * J(A, B) = |A ∩ B| / |A ∪ B|
     */
    private double calculateJaccardSimilarity(Set<Long> setA, Set<Long> setB) {
        if (setA.isEmpty() && setB.isEmpty()) {
            return 0.0;
        }

        Set<Long> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);

        Set<Long> union = new HashSet<>(setA);
        union.addAll(setB);

        return (double) intersection.size() / union.size();
    }

    /**
     * 협업 필터링만 사용한 추천
     */
    public List<Map<String, Object>> getCollaborativeOnlyRecommendations(Long userId, int limit) {
        List<Map<String, Object>> results = getCollaborativeRecommendations(userId, limit);

        // Topic 정보 추가
        return results.stream()
            .map(result -> {
                Long topicId = (Long) result.get("topicId");
                Topic topic = topicRepository.findById(topicId).orElse(null);
                if (topic == null || !topic.getEnabled()) {
                    return null;
                }

                result.put("topicName", topic.getName());
                result.put("topicIcon", topic.getIcon());
                result.put("topicColor", topic.getColor());
                result.put("usageCount", topic.getUsageCount());
                result.put("followerCount", userTopicFollowRepository.countByTopicId(topicId));
                result.put("reason", "유사한 사용자들이 팔로우");

                return result;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 컨텐츠 기반만 사용한 추천
     */
    public List<Map<String, Object>> getContentOnlyRecommendations(Long userId, int limit) {
        List<Map<String, Object>> results = getContentBasedRecommendations(userId, limit);

        // 이미 팔로우한 토픽 제외
        List<Long> followedTopicIds = userTopicFollowRepository.findTopicIdsByUserId(userId);

        // Topic 정보 추가
        return results.stream()
            .filter(result -> !followedTopicIds.contains(result.get("topicId")))
            .map(result -> {
                Long topicId = (Long) result.get("topicId");
                Topic topic = topicRepository.findById(topicId).orElse(null);
                if (topic == null || !topic.getEnabled()) {
                    return null;
                }

                result.put("topicName", topic.getName());
                result.put("topicIcon", topic.getIcon());
                result.put("topicColor", topic.getColor());
                result.put("usageCount", topic.getUsageCount());
                result.put("followerCount", userTopicFollowRepository.countByTopicId(topicId));
                result.put("reason", "활동 기반 추천");

                return result;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}

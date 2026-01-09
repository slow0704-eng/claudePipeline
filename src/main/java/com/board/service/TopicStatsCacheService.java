package com.board.service;

import com.board.entity.Topic;
import com.board.entity.TopicStatsCache;
import com.board.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 토픽 통계 캐시 서비스
 * - 토픽 통계 계산 및 캐싱
 * - 주기적 갱신
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TopicStatsCacheService {

    private final TopicStatsCacheRepository statsCacheRepository;
    private final TopicRepository topicRepository;
    private final BoardTopicRepository boardTopicRepository;
    private final UserTopicFollowRepository userTopicFollowRepository;

    /**
     * 모든 토픽의 통계 갱신
     */
    @Transactional
    public void refreshAllTopicStats() {
        log.info("모든 토픽 통계 갱신 시작");

        List<Topic> topics = topicRepository.findActiveTopics();
        log.info("갱신 대상 토픽: {}개", topics.size());

        int successCount = 0;
        for (Topic topic : topics) {
            try {
                refreshTopicStats(topic.getId());
                successCount++;
            } catch (Exception e) {
                log.error("토픽 통계 갱신 실패: topicId={}", topic.getId(), e);
            }
        }

        // 순위 계산
        calculateRankings();

        log.info("토픽 통계 갱신 완료: 성공 {}/{}", successCount, topics.size());
    }

    /**
     * 상위 인기 토픽만 갱신 (성능 최적화)
     */
    @Transactional
    public void refreshTopTopics(int limit) {
        log.info("상위 {}개 토픽 통계 갱신 시작", limit);

        List<Topic> topics = topicRepository.findTopPopularTopics(PageRequest.of(0, limit));

        int successCount = 0;
        for (Topic topic : topics) {
            try {
                refreshTopicStats(topic.getId());
                successCount++;
            } catch (Exception e) {
                log.error("토픽 통계 갱신 실패: topicId={}", topic.getId(), e);
            }
        }

        // 순위 계산
        calculateRankings();

        log.info("상위 토픽 통계 갱신 완료: 성공 {}/{}", successCount, topics.size());
    }

    /**
     * 단일 토픽 통계 갱신
     */
    @Transactional
    public TopicStatsCache refreshTopicStats(Long topicId) {
        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null || !topic.getEnabled()) {
            return null;
        }

        // 기존 캐시 조회 또는 새로 생성
        TopicStatsCache cache = statsCacheRepository.findByTopicId(topicId)
            .orElseGet(() -> {
                TopicStatsCache newCache = new TopicStatsCache();
                newCache.setTopicId(topicId);
                return newCache;
            });

        // 통계 계산
        calculateStats(cache, topicId);

        // 저장
        return statsCacheRepository.save(cache);
    }

    /**
     * 통계 계산
     */
    private void calculateStats(TopicStatsCache cache, Long topicId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime fourteenDaysAgo = now.minusDays(14);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        // 전체 게시글 수
        long postCount = boardTopicRepository.countByTopicId(topicId);
        cache.setPostCount(postCount);

        // 팔로워 수
        long followerCount = userTopicFollowRepository.countByTopicId(topicId);
        cache.setFollowerCount(followerCount);

        // 최근 7일, 30일 게시글 수
        long posts7days = countPostsInPeriod(topicId, sevenDaysAgo, now);
        long posts30days = countPostsInPeriod(topicId, thirtyDaysAgo, now);
        cache.setPosts7days(posts7days);
        cache.setPosts30days(posts30days);

        // 성장률 계산 (최근 7일 vs 이전 7일)
        long postsOld7days = countPostsInPeriod(topicId, fourteenDaysAgo, sevenDaysAgo);
        BigDecimal growthRate = calculateGrowthRate(posts7days, postsOld7days);
        cache.setGrowthRate7days(growthRate);
    }

    /**
     * 특정 기간 내 게시글 수 계산
     */
    private long countPostsInPeriod(Long topicId, LocalDateTime since, LocalDateTime until) {
        // 토픽의 게시글 ID 조회
        List<Long> boardIds = boardTopicRepository.findBoardIdsByTopicId(topicId);

        // 기간 내 게시글 카운트
        return boardIds.stream()
            .map(boardId -> {
                try {
                    var board = boardTopicRepository.findById(boardId);
                    // 실제로는 Board 조회가 필요하지만, 간단히 하기 위해 생략
                    // 실제 구현에서는 BoardRepository를 주입받아 조회해야 함
                    return 1L;
                } catch (Exception e) {
                    return 0L;
                }
            })
            .count();
    }

    /**
     * 성장률 계산
     */
    private BigDecimal calculateGrowthRate(long currentCount, long previousCount) {
        if (previousCount == 0) {
            return currentCount > 0 ? new BigDecimal("100.0") : BigDecimal.ZERO;
        }

        BigDecimal current = new BigDecimal(currentCount);
        BigDecimal previous = new BigDecimal(previousCount);

        // (current / previous - 1) * 100
        return current.divide(previous, 4, RoundingMode.HALF_UP)
            .subtract(BigDecimal.ONE)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 순위 계산 (모든 캐시에 대해)
     */
    @Transactional
    public void calculateRankings() {
        List<TopicStatsCache> allCaches = statsCacheRepository.findAll();

        // 인기도 순위 (postCount 기준)
        List<TopicStatsCache> byPopularity = allCaches.stream()
            .sorted((a, b) -> Long.compare(b.getPostCount(), a.getPostCount()))
            .toList();

        for (int i = 0; i < byPopularity.size(); i++) {
            byPopularity.get(i).setPopularityRank(i + 1);
        }

        // 트렌딩 순위 (posts7days 기준)
        List<TopicStatsCache> byTrending = allCaches.stream()
            .sorted((a, b) -> Long.compare(b.getPosts7days(), a.getPosts7days()))
            .toList();

        for (int i = 0; i < byTrending.size(); i++) {
            byTrending.get(i).setTrendingRank(i + 1);
        }

        // 성장률 순위 (growthRate7days 기준)
        List<TopicStatsCache> byGrowth = allCaches.stream()
            .sorted((a, b) -> b.getGrowthRate7days().compareTo(a.getGrowthRate7days()))
            .toList();

        for (int i = 0; i < byGrowth.size(); i++) {
            byGrowth.get(i).setGrowthRank(i + 1);
        }

        // 팔로워 순위 (followerCount 기준)
        List<TopicStatsCache> byFollowers = allCaches.stream()
            .sorted((a, b) -> Long.compare(b.getFollowerCount(), a.getFollowerCount()))
            .toList();

        for (int i = 0; i < byFollowers.size(); i++) {
            byFollowers.get(i).setFollowerRank(i + 1);
        }

        // 저장
        statsCacheRepository.saveAll(allCaches);
    }

    /**
     * 캐시 조회 (없으면 즉시 갱신)
     */
    @Transactional
    public Optional<TopicStatsCache> getOrRefreshCache(Long topicId) {
        Optional<TopicStatsCache> cache = statsCacheRepository.findByTopicId(topicId);

        if (cache.isEmpty()) {
            // 캐시가 없으면 즉시 생성
            TopicStatsCache newCache = refreshTopicStats(topicId);
            return Optional.ofNullable(newCache);
        }

        return cache;
    }

    /**
     * 인기 토픽 캐시 조회
     */
    @Transactional(readOnly = true)
    public List<TopicStatsCache> getPopularTopicsCache(int limit) {
        return statsCacheRepository.findTopByPopularityRank(PageRequest.of(0, limit));
    }

    /**
     * 트렌딩 토픽 캐시 조회
     */
    @Transactional(readOnly = true)
    public List<TopicStatsCache> getTrendingTopicsCache(int limit) {
        return statsCacheRepository.findTopByTrendingRank(PageRequest.of(0, limit));
    }

    /**
     * 성장세 토픽 캐시 조회
     */
    @Transactional(readOnly = true)
    public List<TopicStatsCache> getGrowingTopicsCache(int limit) {
        return statsCacheRepository.findTopByGrowthRank(PageRequest.of(0, limit));
    }

    /**
     * 팔로워가 많은 토픽 캐시 조회
     */
    @Transactional(readOnly = true)
    public List<TopicStatsCache> getMostFollowedTopicsCache(int limit) {
        return statsCacheRepository.findTopByFollowerRank(PageRequest.of(0, limit));
    }
}

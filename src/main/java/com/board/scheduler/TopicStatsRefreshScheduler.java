package com.board.scheduler;

import com.board.service.TopicStatsCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 토픽 통계 캐시 갱신 스케줄러
 * - 주기적으로 토픽 통계 캐시 갱신
 * - 성능 최적화를 위한 캐싱
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
    prefix = "topic.stats.cache",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class TopicStatsRefreshScheduler {

    private final TopicStatsCacheService statsCacheService;

    /**
     * 전체 토픽 통계 갱신
     * 기본: 매시간 정각
     * application.properties에서 변경 가능:
     * topic.stats.cache.full-refresh-cron=0 0 * * * ?
     */
    @Scheduled(cron = "${topic.stats.cache.full-refresh-cron:0 0 * * * ?}")
    public void refreshAllTopicStats() {
        log.info("전체 토픽 통계 캐시 갱신 스케줄러 시작");
        try {
            statsCacheService.refreshAllTopicStats();
            log.info("전체 토픽 통계 캐시 갱신 완료");
        } catch (Exception e) {
            log.error("전체 토픽 통계 캐시 갱신 중 오류 발생", e);
        }
    }

    /**
     * 상위 인기 토픽만 빠르게 갱신
     * 기본: 15분마다
     * application.properties에서 변경 가능:
     * topic.stats.cache.quick-refresh-cron=0 *&#47;15 * * * ?
     */
    @Scheduled(cron = "${topic.stats.cache.quick-refresh-cron:0 */15 * * * ?}")
    public void refreshTopTopics() {
        log.info("상위 토픽 통계 캐시 빠른 갱신 시작");
        try {
            // 상위 100개 토픽만 갱신
            int topLimit = getTopTopicsLimit();
            statsCacheService.refreshTopTopics(topLimit);
            log.info("상위 {}개 토픽 통계 캐시 갱신 완료", topLimit);
        } catch (Exception e) {
            log.error("상위 토픽 통계 캐시 갱신 중 오류 발생", e);
        }
    }

    /**
     * 순위 재계산
     * 기본: 30분마다
     * application.properties에서 변경 가능:
     * topic.stats.cache.ranking-refresh-cron=0 *&#47;30 * * * ?
     */
    @Scheduled(cron = "${topic.stats.cache.ranking-refresh-cron:0 */30 * * * ?}")
    public void recalculateRankings() {
        log.info("토픽 순위 재계산 시작");
        try {
            statsCacheService.calculateRankings();
            log.info("토픽 순위 재계산 완료");
        } catch (Exception e) {
            log.error("토픽 순위 재계산 중 오류 발생", e);
        }
    }

    /**
     * 상위 토픽 갱신 제한 수 조회
     */
    private int getTopTopicsLimit() {
        // application.properties에서 설정 가능하도록 개선 가능
        return 100;
    }
}

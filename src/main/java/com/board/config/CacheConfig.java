package com.board.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

/**
 * 캐싱 설정
 * - Caffeine: 인메모리 캐시 (로컬 환경)
 * - Redis: 분산 캐시 (프로덕션 환경, 선택적)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Caffeine 캐시 매니저 (기본)
     * - 고성능 인메모리 캐시
     * - 자동 만료 및 크기 제한
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "categories",        // 카테고리 목록 (거의 변경 안됨)
                "pinnedBoards",      // 고정된 게시글 (자주 조회)
                "popularHashtags",   // 인기 해시태그 (통계)
                "popularTopics",     // 인기 토픽 (통계)
                "dashboardStats",    // 대시보드 통계
                "userProfiles",      // 사용자 프로필
                "boardDetails"       // 게시글 상세 (조회수 많은 글)
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)                          // 최대 1000개 항목
                .expireAfterWrite(10, TimeUnit.MINUTES)     // 쓰기 후 10분 만료
                .recordStats());                            // 통계 기록

        return cacheManager;
    }

    /**
     * Redis 캐시 매니저 (프로덕션 환경, 선택적)
     * - 분산 환경에서 사용
     * - 여러 서버 간 캐시 공유
     *
     * 사용하려면:
     * 1. application.yml에 Redis 설정 추가
     * 2. @Profile("prod") 활성화
     * 3. @Primary를 caffeineCacheManager에서 이 메서드로 이동
     */
    /*
    @Bean
    @Profile("prod")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
    */
}

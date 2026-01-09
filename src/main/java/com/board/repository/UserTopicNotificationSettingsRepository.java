package com.board.repository;

import com.board.entity.UserTopicNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopicNotificationSettingsRepository extends JpaRepository<UserTopicNotificationSettings, Long> {

    /**
     * 사용자 ID로 설정 조회
     */
    Optional<UserTopicNotificationSettings> findByUserId(Long userId);

    /**
     * 전역 알림이 활성화된 사용자 조회
     */
    @Query("SELECT s FROM UserTopicNotificationSettings s " +
           "WHERE s.globalNotificationEnabled = true")
    List<UserTopicNotificationSettings> findAllWithNotificationEnabled();

    /**
     * 특정 빈도의 알림이 활성화된 사용자 조회
     */
    @Query("SELECT s FROM UserTopicNotificationSettings s " +
           "WHERE s.globalNotificationEnabled = true " +
           "AND s.globalFrequency = :frequency")
    List<UserTopicNotificationSettings> findByGlobalFrequency(@Param("frequency") String frequency);

    /**
     * 이메일 알림이 활성화된 사용자 조회
     */
    @Query("SELECT s FROM UserTopicNotificationSettings s " +
           "WHERE s.globalNotificationEnabled = true " +
           "AND s.globalEmailEnabled = true " +
           "AND s.globalFrequency = :frequency")
    List<UserTopicNotificationSettings> findByEmailEnabledAndFrequency(@Param("frequency") String frequency);

    /**
     * 일간 다이제스트 대상 사용자 조회 (특정 시간대)
     */
    @Query("SELECT s FROM UserTopicNotificationSettings s " +
           "WHERE s.globalNotificationEnabled = true " +
           "AND s.globalFrequency = 'DAILY' " +
           "AND s.preferredTime = :time")
    List<UserTopicNotificationSettings> findDailyDigestUsers(@Param("time") java.time.LocalTime time);

    /**
     * 주간 다이제스트 대상 사용자 조회 (특정 요일, 시간)
     */
    @Query("SELECT s FROM UserTopicNotificationSettings s " +
           "WHERE s.globalNotificationEnabled = true " +
           "AND s.globalFrequency = 'WEEKLY' " +
           "AND s.preferredDayOfWeek = :dayOfWeek " +
           "AND s.preferredTime = :time")
    List<UserTopicNotificationSettings> findWeeklyDigestUsers(
        @Param("dayOfWeek") Integer dayOfWeek,
        @Param("time") java.time.LocalTime time
    );

    /**
     * 사용자 ID 존재 여부 확인
     */
    boolean existsByUserId(Long userId);
}

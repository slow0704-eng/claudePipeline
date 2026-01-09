package com.board.controller;

import com.board.entity.User;
import com.board.entity.UserTopicNotificationSettings;
import com.board.service.TopicNotificationService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TopicNotificationController {

    private final TopicNotificationService notificationService;
    private final UserService userService;

    /**
     * 알림 설정 페이지
     * GET /mypage/topic-notifications
     */
    @GetMapping("/mypage/topic-notifications")
    public String notificationSettingsPage(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // 사용자 알림 설정 조회
        UserTopicNotificationSettings settings = notificationService.getUserSettings(currentUser.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("settings", settings);

        return "mypage/topic-notifications";
    }

    /**
     * 알림 설정 조회 API
     * GET /api/topics/notifications/settings
     */
    @GetMapping("/api/topics/notifications/settings")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNotificationSettings() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            UserTopicNotificationSettings settings = notificationService.getUserSettings(currentUser.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("settings", settingsToMap(settings));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 알림 설정 업데이트 API
     * PUT /api/topics/notifications/settings
     */
    @PutMapping("/api/topics/notifications/settings")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateNotificationSettings(@RequestBody Map<String, Object> payload) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            // 요청 데이터를 엔티티로 변환
            UserTopicNotificationSettings newSettings = new UserTopicNotificationSettings();

            if (payload.containsKey("globalNotificationEnabled")) {
                newSettings.setGlobalNotificationEnabled((Boolean) payload.get("globalNotificationEnabled"));
            }

            if (payload.containsKey("globalFrequency")) {
                newSettings.setGlobalFrequency((String) payload.get("globalFrequency"));
            }

            if (payload.containsKey("globalEmailEnabled")) {
                newSettings.setGlobalEmailEnabled((Boolean) payload.get("globalEmailEnabled"));
            }

            if (payload.containsKey("preferredTime")) {
                String timeStr = (String) payload.get("preferredTime");
                newSettings.setPreferredTime(LocalTime.parse(timeStr));
            }

            if (payload.containsKey("preferredDayOfWeek")) {
                newSettings.setPreferredDayOfWeek(((Number) payload.get("preferredDayOfWeek")).intValue());
            }

            if (payload.containsKey("topicSpecificSettings")) {
                newSettings.setTopicSpecificSettings((String) payload.get("topicSpecificSettings"));
            }

            // 설정 업데이트
            UserTopicNotificationSettings updated = notificationService.updateUserSettings(
                currentUser.getId(),
                newSettings
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("settings", settingsToMap(updated));
            response.put("message", "알림 설정이 업데이트되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 전역 알림 활성화/비활성화 API (간편 토글)
     * POST /api/topics/notifications/toggle
     */
    @PostMapping("/api/topics/notifications/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleNotification() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            UserTopicNotificationSettings settings = notificationService.getUserSettings(currentUser.getId());
            settings.setGlobalNotificationEnabled(!settings.getGlobalNotificationEnabled());

            UserTopicNotificationSettings updated = notificationService.updateUserSettings(
                currentUser.getId(),
                settings
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("enabled", updated.getGlobalNotificationEnabled());
            response.put("message", updated.getGlobalNotificationEnabled() ? "알림이 활성화되었습니다." : "알림이 비활성화되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Settings 엔티티를 Map으로 변환
     */
    private Map<String, Object> settingsToMap(UserTopicNotificationSettings settings) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", settings.getId());
        map.put("userId", settings.getUserId());
        map.put("globalNotificationEnabled", settings.getGlobalNotificationEnabled());
        map.put("globalFrequency", settings.getGlobalFrequency());
        map.put("globalEmailEnabled", settings.getGlobalEmailEnabled());
        map.put("topicSpecificSettings", settings.getTopicSpecificSettings());
        map.put("preferredTime", settings.getPreferredTime() != null ? settings.getPreferredTime().toString() : "09:00");
        map.put("preferredDayOfWeek", settings.getPreferredDayOfWeek());
        map.put("createdAt", settings.getCreatedAt());
        map.put("updatedAt", settings.getUpdatedAt());
        return map;
    }
}

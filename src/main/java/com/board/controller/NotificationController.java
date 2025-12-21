package com.board.controller;

import com.board.entity.Notification;
import com.board.entity.User;
import com.board.service.NotificationService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/notifications")
    public String notifications(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        List<Notification> notifications = notificationService.getAllNotifications(currentUser.getId());
        long unreadCount = notificationService.getUnreadCount(currentUser.getId());

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("user", currentUser);

        return "notification/list";
    }

    @GetMapping("/api/notifications/count")
    @ResponseBody
    public Map<String, Object> getUnreadCount() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        Map<String, Object> result = new HashMap<>();

        if (currentUser != null) {
            long count = notificationService.getUnreadCount(currentUser.getId());
            result.put("count", count);
        } else {
            result.put("count", 0);
        }

        return result;
    }

    @GetMapping("/api/notifications/recent")
    @ResponseBody
    public Map<String, Object> getRecentNotifications(@RequestParam(defaultValue = "10") int limit) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        Map<String, Object> result = new HashMap<>();

        if (currentUser != null) {
            List<Notification> notifications = notificationService.getRecentNotifications(currentUser.getId(), limit);
            result.put("notifications", notifications);
        } else {
            result.put("notifications", List.of());
        }

        return result;
    }

    @PostMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/notifications/read-all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        try {
            notificationService.markAllAsRead(currentUser.getId());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/api/notifications/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/api/notifications")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAllNotifications() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        try {
            notificationService.deleteAllNotifications(currentUser.getId());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}

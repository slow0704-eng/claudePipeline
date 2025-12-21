package com.board.controller;

import com.board.entity.User;
import com.board.enums.ReportReason;
import com.board.enums.ReportTargetType;
import com.board.service.ReportService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    /**
     * 신고 생성 API
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReport(
            @RequestParam("targetType") ReportTargetType targetType,
            @RequestParam("targetId") Long targetId,
            @RequestParam("reason") ReportReason reason,
            @RequestParam(value = "description", required = false) String description) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }

        try {
            Map<String, Object> result = reportService.createReport(
                currentUser.getId(),
                targetType,
                targetId,
                reason,
                description
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 신고 여부 확인 API
     */
    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkReport(
            @RequestParam("targetType") ReportTargetType targetType,
            @RequestParam("targetId") Long targetId) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.ok(Map.of("isReported", false));
        }

        boolean isReported = reportService.isReported(currentUser.getId(), targetType, targetId);
        return ResponseEntity.ok(Map.of("isReported", isReported));
    }
}

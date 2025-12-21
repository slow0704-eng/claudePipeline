package com.board.controller;

import com.board.entity.Message;
import com.board.entity.User;
import com.board.repository.UserRepository;
import com.board.service.MessageService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * 메시지함 (대화 목록)
     */
    @GetMapping
    public String inbox(Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        List<Map<String, Object>> conversations = messageService.getConversationList(currentUser.getId());
        long unreadCount = messageService.getUnreadMessageCount(currentUser.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("conversations", conversations);
        model.addAttribute("unreadCount", unreadCount);

        return "messages/inbox";
    }

    /**
     * 대화 상세 페이지
     */
    @GetMapping("/conversation/{partnerId}")
    public String conversation(@PathVariable Long partnerId, Model model) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        User partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Message> messages = messageService.getConversation(currentUser.getId(), partnerId);

        // 대화 읽음 처리
        messageService.markConversationAsRead(currentUser.getId(), partnerId);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("partner", partner);
        model.addAttribute("messages", messages);

        return "messages/conversation";
    }

    /**
     * 메시지 전송 API
     */
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam("recipientId") Long recipientId,
            @RequestParam("content") String content) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "메시지 내용을 입력하세요."
            ));
        }

        if (content.length() > 1000) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "메시지는 1000자 이내로 작성해주세요."
            ));
        }

        try {
            Message message = messageService.sendMessage(currentUser.getId(), recipientId, content.trim());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "메시지가 전송되었습니다.",
                "messageId", message.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 메시지 읽음 처리 API
     */
    @PostMapping("/{messageId}/read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long messageId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }

        try {
            messageService.markAsRead(messageId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "읽음 처리되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 메시지 삭제 API
     */
    @DeleteMapping("/{messageId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long messageId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }

        try {
            messageService.deleteMessage(messageId, currentUser.getId());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "메시지가 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 대화 삭제 API
     */
    @DeleteMapping("/conversation/{partnerId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteConversation(@PathVariable Long partnerId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }

        try {
            messageService.deleteConversation(currentUser.getId(), partnerId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "대화가 삭제되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 읽지 않은 메시지 개수 API
     */
    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return ResponseEntity.ok(Map.of("unreadCount", 0));
        }

        long unreadCount = messageService.getUnreadMessageCount(currentUser.getId());
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    /**
     * 받은 메시지함
     */
    @GetMapping("/received")
    public String receivedMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        Page<Message> messages = messageService.getReceivedMessages(currentUser.getId(), page, size);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("messagePage", messages);
        model.addAttribute("pageType", "received");

        return "messages/list";
    }

    /**
     * 보낸 메시지함
     */
    @GetMapping("/sent")
    public String sentMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        Page<Message> messages = messageService.getSentMessages(currentUser.getId(), page, size);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("messagePage", messages);
        model.addAttribute("pageType", "sent");

        return "messages/list";
    }
}

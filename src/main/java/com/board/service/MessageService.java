package com.board.service;

import com.board.entity.Message;
import com.board.entity.User;
import com.board.repository.MessageRepository;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    /**
     * 메시지 전송
     */
    @Transactional
    public Message sendMessage(Long senderId, Long recipientId, String content) {
        // 자기 자신에게 메시지 전송 불가
        if (senderId.equals(recipientId)) {
            throw new RuntimeException("자기 자신에게 메시지를 보낼 수 없습니다.");
        }

        // 수신자 존재 여부 확인
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        // 수신자가 비활성화된 경우
        if (!recipient.isEnabled()) {
            throw new RuntimeException("비활성화된 사용자에게 메시지를 보낼 수 없습니다.");
        }

        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setContent(content);
        message.setIsRead(false);

        return messageRepository.save(message);
    }

    /**
     * 두 사용자 간의 대화 조회
     */
    @Transactional(readOnly = true)
    public List<Message> getConversation(Long userId1, Long userId2) {
        List<Message> messages = messageRepository.findConversation(userId1, userId2);

        // 닉네임 설정
        Map<Long, String> nicknameCache = new HashMap<>();

        for (Message message : messages) {
            if (!nicknameCache.containsKey(message.getSenderId())) {
                User sender = userRepository.findById(message.getSenderId()).orElse(null);
                nicknameCache.put(message.getSenderId(), sender != null ? sender.getNickname() : "탈퇴한 사용자");
            }
            if (!nicknameCache.containsKey(message.getRecipientId())) {
                User recipient = userRepository.findById(message.getRecipientId()).orElse(null);
                nicknameCache.put(message.getRecipientId(), recipient != null ? recipient.getNickname() : "탈퇴한 사용자");
            }

            message.setSenderNickname(nicknameCache.get(message.getSenderId()));
            message.setRecipientNickname(nicknameCache.get(message.getRecipientId()));
        }

        return messages;
    }

    /**
     * 읽지 않은 메시지 조회
     */
    @Transactional(readOnly = true)
    public List<Message> getUnreadMessages(Long userId) {
        List<Message> messages = messageRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        // 발신자 닉네임 설정
        for (Message message : messages) {
            User sender = userRepository.findById(message.getSenderId()).orElse(null);
            message.setSenderNickname(sender != null ? sender.getNickname() : "탈퇴한 사용자");
        }

        return messages;
    }

    /**
     * 읽지 않은 메시지 개수
     */
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long userId) {
        return messageRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    /**
     * 메시지 읽음 처리
     */
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));

        if (!message.getIsRead()) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    /**
     * 대화 전체 읽음 처리
     */
    @Transactional
    public void markConversationAsRead(Long currentUserId, Long otherUserId) {
        List<Message> unreadMessages = messageRepository.findConversation(currentUserId, otherUserId)
                .stream()
                .filter(m -> m.getRecipientId().equals(currentUserId) && !m.getIsRead())
                .collect(Collectors.toList());

        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
        }

        if (!unreadMessages.isEmpty()) {
            messageRepository.saveAll(unreadMessages);
        }
    }

    /**
     * 메시지 삭제
     */
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));

        // 발신자나 수신자만 삭제 가능
        if (!message.getSenderId().equals(userId) && !message.getRecipientId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        messageRepository.delete(message);
    }

    /**
     * 대화 전체 삭제
     */
    @Transactional
    public void deleteConversation(Long userId1, Long userId2) {
        messageRepository.deleteConversation(userId1, userId2);
    }

    /**
     * 받은 메시지함 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<Message> getReceivedMessages(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable);

        // 발신자 닉네임 설정
        messages.forEach(message -> {
            User sender = userRepository.findById(message.getSenderId()).orElse(null);
            message.setSenderNickname(sender != null ? sender.getNickname() : "탈퇴한 사용자");
        });

        return messages;
    }

    /**
     * 보낸 메시지함 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<Message> getSentMessages(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findBySenderIdOrderByCreatedAtDesc(userId, pageable);

        // 수신자 닉네임 설정
        messages.forEach(message -> {
            User recipient = userRepository.findById(message.getRecipientId()).orElse(null);
            message.setRecipientNickname(recipient != null ? recipient.getNickname() : "탈퇴한 사용자");
        });

        return messages;
    }

    /**
     * 대화 목록 조회 (최근 대화 상대)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getConversationList(Long userId) {
        List<Long> partnerIds = messageRepository.findConversationPartners(userId);
        List<Map<String, Object>> conversations = new ArrayList<>();

        for (Long partnerId : partnerIds) {
            User partner = userRepository.findById(partnerId).orElse(null);
            if (partner == null) continue;

            // 최근 메시지 조회
            List<Message> lastMessages = messageRepository.findLastMessageBetweenUsers(
                userId, partnerId, PageRequest.of(0, 1)
            );

            if (lastMessages.isEmpty()) continue;

            Message lastMessage = lastMessages.get(0);

            // 읽지 않은 메시지 개수 (상대방이 보낸 것 중)
            long unreadCount = messageRepository.findConversation(userId, partnerId)
                    .stream()
                    .filter(m -> m.getRecipientId().equals(userId) && !m.getIsRead())
                    .count();

            Map<String, Object> conversation = new HashMap<>();
            conversation.put("partnerId", partnerId);
            conversation.put("partnerNickname", partner.getNickname());
            conversation.put("lastMessage", lastMessage.getContent());
            conversation.put("lastMessageTime", lastMessage.getCreatedAt());
            conversation.put("unreadCount", unreadCount);
            conversation.put("isLastMessageFromMe", lastMessage.getSenderId().equals(userId));

            conversations.add(conversation);
        }

        // 최근 메시지 시간 기준 정렬
        conversations.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("lastMessageTime");
            LocalDateTime timeB = (LocalDateTime) b.get("lastMessageTime");
            return timeB.compareTo(timeA);
        });

        return conversations;
    }
}

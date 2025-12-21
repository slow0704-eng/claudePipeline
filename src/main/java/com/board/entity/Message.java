package com.board.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "messages",
       indexes = {
           @Index(name = "idx_message_recipient", columnList = "recipient_id, is_read, created_at DESC"),
           @Index(name = "idx_message_conversation", columnList = "sender_id, recipient_id, created_at DESC")
       })
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 발신자 닉네임 (조회용)
    @Transient
    private String senderNickname;

    // 수신자 닉네임 (조회용)
    @Transient
    private String recipientNickname;
}

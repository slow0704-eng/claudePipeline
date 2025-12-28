package com.board.repository;

import com.board.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 특정 사용자가 받은 메시지 조회 (페이징)
     */
    Page<Message> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    /**
     * 특정 사용자가 보낸 메시지 조회 (페이징)
     */
    Page<Message> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);

    /**
     * 두 사용자 간의 대화 조회 (양방향)
     */
    @Query("SELECT m FROM Message m " +
           "WHERE (m.senderId = :userId1 AND m.recipientId = :userId2) " +
           "OR (m.senderId = :userId2 AND m.recipientId = :userId1) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 읽지 않은 메시지 조회
     */
    List<Message> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    /**
     * 읽지 않은 메시지 개수
     */
    long countByRecipientIdAndIsReadFalse(Long recipientId);

    /**
     * 대화 상대 목록 조회 (최근 메시지 기준)
     * PostgreSQL의 DISTINCT + ORDER BY 제약을 피하기 위해 서브쿼리 사용
     */
    @Query("SELECT CASE " +
           "WHEN m.senderId = :userId THEN m.recipientId " +
           "ELSE m.senderId END " +
           "FROM Message m " +
           "WHERE m.senderId = :userId OR m.recipientId = :userId " +
           "GROUP BY CASE WHEN m.senderId = :userId THEN m.recipientId ELSE m.senderId END " +
           "ORDER BY MAX(m.createdAt) DESC")
    List<Long> findConversationPartners(@Param("userId") Long userId);

    /**
     * 특정 사용자와의 마지막 메시지 조회
     */
    @Query("SELECT m FROM Message m " +
           "WHERE (m.senderId = :userId1 AND m.recipientId = :userId2) " +
           "OR (m.senderId = :userId2 AND m.recipientId = :userId1) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findLastMessageBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);

    /**
     * 특정 사용자가 주고받은 모든 메시지 삭제
     */
    void deleteBySenderIdOrRecipientId(Long senderId, Long recipientId);

    /**
     * 특정 사용자와의 대화 삭제
     */
    @Query("DELETE FROM Message m " +
           "WHERE (m.senderId = :userId1 AND m.recipientId = :userId2) " +
           "OR (m.senderId = :userId2 AND m.recipientId = :userId1)")
    void deleteConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}

package com.board.repository;

import com.board.entity.BoardTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardTopicRepository extends JpaRepository<BoardTopic, Long> {

    // 기본 쿼리
    List<BoardTopic> findByBoardId(Long boardId);
    List<BoardTopic> findByTopicId(Long topicId);

    @Query("SELECT bt.topicId FROM BoardTopic bt WHERE bt.boardId = :boardId")
    List<Long> findTopicIdsByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT bt.boardId FROM BoardTopic bt WHERE bt.topicId = :topicId " +
           "ORDER BY bt.createdAt DESC")
    List<Long> findBoardIdsByTopicId(@Param("topicId") Long topicId);

    // Count 쿼리
    long countByBoardId(Long boardId);
    long countByTopicId(Long topicId);

    // 존재 확인
    boolean existsByBoardIdAndTopicId(Long boardId, Long topicId);

    // Delete 쿼리
    @Modifying
    @Query("DELETE FROM BoardTopic bt WHERE bt.boardId = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);

    @Modifying
    @Query("DELETE FROM BoardTopic bt WHERE bt.topicId = :topicId")
    void deleteByTopicId(@Param("topicId") Long topicId);

    // Co-occurrence 분석 (관련 토픽)
    @Query("SELECT bt2.topicId, COUNT(bt2.topicId) as frequency " +
           "FROM BoardTopic bt1 " +
           "JOIN BoardTopic bt2 ON bt1.boardId = bt2.boardId " +
           "WHERE bt1.topicId = :topicId AND bt2.topicId != :topicId " +
           "GROUP BY bt2.topicId " +
           "ORDER BY frequency DESC")
    List<Object[]> findRelatedTopicIds(@Param("topicId") Long topicId);

    // 토픽 빈도 통계
    @Query("SELECT t.id, t.name, COUNT(bt.id) as frequency " +
           "FROM BoardTopic bt " +
           "JOIN Topic t ON bt.topicId = t.id " +
           "WHERE t.enabled = true " +
           "GROUP BY t.id, t.name " +
           "ORDER BY frequency DESC")
    List<Object[]> findAllTopicFrequencies();
}

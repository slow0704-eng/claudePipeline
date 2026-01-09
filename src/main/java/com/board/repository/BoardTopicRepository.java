package com.board.repository;

import com.board.entity.BoardTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글-주제 매핑 리포지토리
 */
@Repository
public interface BoardTopicRepository extends JpaRepository<BoardTopic, Long> {

    /**
     * 게시글의 모든 주제 조회
     */
    List<BoardTopic> findByBoardId(Long boardId);

    /**
     * 특정 주제를 가진 모든 게시글 조회
     */
    List<BoardTopic> findByTopicId(Long topicId);

    /**
     * 게시글의 모든 주제 삭제
     */
    @Modifying
    @Query("DELETE FROM BoardTopic bt WHERE bt.boardId = :boardId")
    void deleteByBoardId(Long boardId);

    /**
     * 특정 게시글-주제 매핑 삭제
     */
    @Modifying
    @Query("DELETE FROM BoardTopic bt WHERE bt.boardId = :boardId AND bt.topicId = :topicId")
    void deleteByBoardIdAndTopicId(Long boardId, Long topicId);

    /**
     * 게시글의 주제 개수 조회
     */
    long countByBoardId(Long boardId);

    /**
     * 특정 주제를 가진 게시글 개수 조회
     */
    long countByTopicId(Long topicId);
}

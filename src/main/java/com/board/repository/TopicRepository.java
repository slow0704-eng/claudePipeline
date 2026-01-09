package com.board.repository;

import com.board.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 주제(토픽) 리포지토리
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    /**
     * 이름으로 주제 조회
     */
    Optional<Topic> findByName(String name);

    /**
     * 활성화된 주제 목록 조회
     */
    List<Topic> findByActiveTrue();

    /**
     * 인기 주제 목록 조회 (사용 횟수 기준)
     */
    @Query("SELECT t FROM Topic t WHERE t.active = true ORDER BY t.usageCount DESC")
    List<Topic> findPopularTopics(Pageable pageable);

    /**
     * 주제명으로 검색 (부분 일치)
     */
    @Query("SELECT t FROM Topic t WHERE t.active = true AND LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Topic> searchByName(String keyword);
}

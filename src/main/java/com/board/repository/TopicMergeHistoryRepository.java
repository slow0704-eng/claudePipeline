package com.board.repository;

import com.board.entity.TopicMergeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicMergeHistoryRepository extends JpaRepository<TopicMergeHistory, Long> {

    List<TopicMergeHistory> findBySourceTopicIdOrderByMergedAtDesc(Long sourceTopicId);
    List<TopicMergeHistory> findByTargetTopicIdOrderByMergedAtDesc(Long targetTopicId);

    @Query("SELECT tmh FROM TopicMergeHistory tmh ORDER BY tmh.mergedAt DESC")
    List<TopicMergeHistory> findAllOrderByMergedAtDesc();
}

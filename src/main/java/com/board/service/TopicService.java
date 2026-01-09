package com.board.service;

import com.board.entity.Topic;
import com.board.exception.ResourceNotFoundException;
import com.board.exception.ErrorCode;
import com.board.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 주제(토픽) 서비스
 * 주제 관리 및 조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {

    private final TopicRepository topicRepository;

    /**
     * 모든 활성화된 주제 조회
     */
    public List<Topic> getAllActiveTopics() {
        return topicRepository.findByActiveTrue();
    }

    /**
     * 주제 ID로 조회
     */
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TOPIC_NOT_FOUND, "주제를 찾을 수 없습니다."));
    }

    /**
     * 주제명으로 조회
     */
    public Topic getTopicByName(String name) {
        return topicRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TOPIC_NOT_FOUND, "주제를 찾을 수 없습니다: " + name));
    }

    /**
     * 주제명으로 조회 또는 생성
     */
    @Transactional
    public Topic getOrCreateTopic(String name) {
        return topicRepository.findByName(name)
                .orElseGet(() -> createTopic(name, null));
    }

    /**
     * 인기 주제 목록 조회
     */
    @Cacheable(value = "popularTopics", key = "#limit")
    public List<Topic> getPopularTopics(int limit) {
        return topicRepository.findPopularTopics(PageRequest.of(0, limit));
    }

    /**
     * 주제명으로 검색
     */
    public List<Topic> searchTopics(String keyword) {
        return topicRepository.searchByName(keyword);
    }

    /**
     * 새 주제 생성
     */
    @Transactional
    @CacheEvict(value = "popularTopics", allEntries = true)
    public Topic createTopic(String name, String description) {
        Topic topic = Topic.builder().build();
        topic.setName(name);
        topic.setDescription(description);
        return topicRepository.save(topic);
    }

    /**
     * 주제 정보 수정
     */
    @Transactional
    @CacheEvict(value = "popularTopics", allEntries = true)
    public Topic updateTopic(Long id, String name, String description) {
        Topic topic = getTopicById(id);
        if (name != null) {
            topic.setName(name);
        }
        if (description != null) {
            topic.setDescription(description);
        }
        return topicRepository.save(topic);
    }

    /**
     * 주제 삭제 (비활성화)
     */
    @Transactional
    @CacheEvict(value = "popularTopics", allEntries = true)
    public void deleteTopic(Long id) {
        Topic topic = getTopicById(id);
        topic.deactivate();
        topicRepository.save(topic);
    }

    /**
     * 주제 사용 횟수 증가
     */
    @Transactional
    @CacheEvict(value = "popularTopics", allEntries = true)
    public void incrementUsageCount(Long topicId) {
        Topic topic = getTopicById(topicId);
        topic.incrementUsageCount();
        topicRepository.save(topic);
    }

    /**
     * 주제 사용 횟수 감소
     */
    @Transactional
    @CacheEvict(value = "popularTopics", allEntries = true)
    public void decrementUsageCount(Long topicId) {
        Topic topic = getTopicById(topicId);
        topic.decrementUsageCount();
        topicRepository.save(topic);
    }
}

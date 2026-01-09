package com.board.service;

import com.board.entity.BoardTopic;
import com.board.entity.Topic;
import com.board.repository.BoardTopicRepository;
import com.board.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardTopicService {

    private final BoardTopicRepository boardTopicRepository;
    private final TopicRepository topicRepository;

    private static final int MAX_TOPICS_PER_BOARD = 5;

    @Transactional
    public void linkBoardToTopic(Long boardId, Long topicId) {
        // Check max topics limit
        long currentCount = boardTopicRepository.countByBoardId(boardId);
        if (currentCount >= MAX_TOPICS_PER_BOARD) {
            throw new RuntimeException("게시글당 최대 " + MAX_TOPICS_PER_BOARD + "개의 토픽만 추가할 수 있습니다.");
        }

        // Check if already linked
        if (boardTopicRepository.existsByBoardIdAndTopicId(boardId, topicId)) {
            return; // Already linked
        }

        // Create link
        BoardTopic boardTopic = new BoardTopic();
        boardTopic.setBoardId(boardId);
        boardTopic.setTopicId(topicId);
        boardTopicRepository.save(boardTopic);

        // Increment usage count
        topicRepository.findById(topicId).ifPresent(topic -> {
            topic.incrementUsageCount();
            topicRepository.save(topic);
        });
    }

    @Transactional
    public void unlinkBoardFromTopic(Long boardId, Long topicId) {
        List<BoardTopic> links = boardTopicRepository.findByBoardId(boardId).stream()
                .filter(bt -> bt.getTopicId().equals(topicId))
                .collect(Collectors.toList());

        for (BoardTopic link : links) {
            boardTopicRepository.delete(link);
        }

        // Decrement usage count
        topicRepository.findById(topicId).ifPresent(topic -> {
            topic.decrementUsageCount();
            topicRepository.save(topic);
        });
    }

    @Transactional
    public void updateBoardTopics(Long boardId, List<Long> topicIds) {
        // Validate max topics
        if (topicIds.size() > MAX_TOPICS_PER_BOARD) {
            throw new RuntimeException("게시글당 최대 " + MAX_TOPICS_PER_BOARD + "개의 토픽만 추가할 수 있습니다.");
        }

        // Remove all existing links and decrement counts
        removeAllTopicsFromBoard(boardId);

        // Add new links
        for (Long topicId : topicIds) {
            linkBoardToTopic(boardId, topicId);
        }
    }

    @Transactional
    public void removeAllTopicsFromBoard(Long boardId) {
        List<Long> topicIds = boardTopicRepository.findTopicIdsByBoardId(boardId);

        // Decrement usage counts
        for (Long topicId : topicIds) {
            topicRepository.findById(topicId).ifPresent(topic -> {
                topic.decrementUsageCount();
                topicRepository.save(topic);
            });
        }

        // Delete relationships
        boardTopicRepository.deleteByBoardId(boardId);
    }

    public List<Topic> getBoardTopics(Long boardId) {
        List<Long> topicIds = boardTopicRepository.findTopicIdsByBoardId(boardId);
        return topicIds.stream()
                .map(id -> topicRepository.findById(id).orElse(null))
                .filter(topic -> topic != null)
                .collect(Collectors.toList());
    }

    public List<String> getBoardTopicPaths(Long boardId) {
        List<Topic> topics = getBoardTopics(boardId);
        return topics.stream()
                .map(this::buildTopicPath)
                .collect(Collectors.toList());
    }

    private String buildTopicPath(Topic topic) {
        StringBuilder path = new StringBuilder();
        buildPathRecursive(topic, path);
        return path.toString();
    }

    private void buildPathRecursive(Topic topic, StringBuilder path) {
        if (topic.getParentId() != null) {
            topicRepository.findById(topic.getParentId()).ifPresent(parent -> {
                buildPathRecursive(parent, path);
                path.append(" > ");
            });
        }
        path.append(topic.getName());
    }

    public long countBoardTopics(Long boardId) {
        return boardTopicRepository.countByBoardId(boardId);
    }

    public boolean canAddMoreTopics(Long boardId) {
        return countBoardTopics(boardId) < MAX_TOPICS_PER_BOARD;
    }
}

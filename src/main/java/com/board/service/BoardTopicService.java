package com.board.service;

import com.board.entity.Board;
import com.board.entity.BoardTopic;
import com.board.entity.Topic;
import com.board.repository.BoardTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글-주제 매핑 서비스
 * 게시글과 주제의 연결을 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardTopicService {

    private final BoardTopicRepository boardTopicRepository;
    private final TopicService topicService;

    /**
     * 게시글의 모든 주제 조회
     */
    public List<Topic> getTopicsByBoardId(Long boardId) {
        return boardTopicRepository.findByBoardId(boardId).stream()
                .map(BoardTopic::getTopic)
                .collect(Collectors.toList());
    }

    /**
     * 특정 주제를 가진 모든 게시글 조회
     */
    public List<Board> getBoardsByTopicId(Long topicId) {
        return boardTopicRepository.findByTopicId(topicId).stream()
                .map(BoardTopic::getBoard)
                .collect(Collectors.toList());
    }

    /**
     * 게시글에 주제 추가
     */
    @Transactional
    public BoardTopic addTopicToBoard(Board board, Topic topic) {
        BoardTopic boardTopic = new BoardTopic(board, topic);
        BoardTopic saved = boardTopicRepository.save(boardTopic);

        // 주제 사용 횟수 증가
        topicService.incrementUsageCount(topic.getId());

        return saved;
    }

    /**
     * 게시글에 여러 주제 추가
     */
    @Transactional
    public void addTopicsToBoard(Board board, List<String> topicNames) {
        for (String topicName : topicNames) {
            Topic topic = topicService.getOrCreateTopic(topicName);
            addTopicToBoard(board, topic);
        }
    }

    /**
     * 게시글에서 주제 제거
     */
    @Transactional
    public void removeTopicFromBoard(Long boardId, Long topicId) {
        boardTopicRepository.deleteByBoardIdAndTopicId(boardId, topicId);

        // 주제 사용 횟수 감소
        topicService.decrementUsageCount(topicId);
    }

    /**
     * 게시글의 모든 주제 제거
     */
    @Transactional
    public void removeAllTopicsFromBoard(Long boardId) {
        List<BoardTopic> boardTopics = boardTopicRepository.findByBoardId(boardId);

        // 각 주제의 사용 횟수 감소
        for (BoardTopic boardTopic : boardTopics) {
            topicService.decrementUsageCount(boardTopic.getTopicId());
        }

        boardTopicRepository.deleteByBoardId(boardId);
    }

    /**
     * 게시글의 주제 업데이트 (기존 주제 제거 후 새 주제 추가)
     */
    @Transactional
    public void updateBoardTopics(Board board, List<String> topicNames) {
        // 기존 주제 제거
        removeAllTopicsFromBoard(board.getId());

        // 새 주제 추가
        if (topicNames != null && !topicNames.isEmpty()) {
            addTopicsToBoard(board, topicNames);
        }
    }

    /**
     * 게시글의 주제 개수 조회
     */
    public long countTopicsByBoardId(Long boardId) {
        return boardTopicRepository.countByBoardId(boardId);
    }

    /**
     * 특정 주제를 가진 게시글 개수 조회
     */
    public long countBoardsByTopicId(Long topicId) {
        return boardTopicRepository.countByTopicId(topicId);
    }
}

    /**
     * 게시글의 모든 주제 조회 (별칭 메서드)
     */
    public List<Topic> getBoardTopics(Long boardId) {
        return getTopicsByBoardId(boardId);
    }

    /**
     * 게시글의 주제 경로 목록 조회 (주제명 문자열 리스트)
     */
    public List<String> getBoardTopicPaths(Long boardId) {
        return getTopicsByBoardId(boardId).stream()
                .map(Topic::getName)
                .collect(Collectors.toList());

    /**
     * 게시글의 모든 주제 조회 (별칭 메서드)
     */
    public List<Topic> getBoardTopics(Long boardId) {
        return getTopicsByBoardId(boardId);
    }

    /**
     * 게시글의 주제 경로 목록 조회 (주제명 문자열 리스트)
     */
    public List<String> getBoardTopicPaths(Long boardId) {
        return getTopicsByBoardId(boardId).stream()
                .map(Topic::getName)
                .collect(Collectors.toList());
    }

    /**
     * 게시글의 주제 업데이트 (ID 버전)
     */
    @Transactional
    public void updateBoardTopics(Long boardId, List<Long> topicIds) {
        // 기존 주제 제거
        removeAllTopicsFromBoard(boardId);

        // 새 주제 추가
        if (topicIds != null && !topicIds.isEmpty()) {
            for (Long topicId : topicIds) {
                Topic topic = topicService.getTopicById(topicId);
                Board board = new Board();
                board.setId(boardId);
                addTopicToBoard(board, topic);
            }
        }
    }
}

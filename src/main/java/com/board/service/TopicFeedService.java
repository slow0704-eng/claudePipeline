package com.board.service;

import com.board.dto.TopicFeedItemDTO;
import com.board.entity.Board;
import com.board.entity.Share;
import com.board.entity.Topic;
import com.board.entity.User;
import com.board.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 토픽 피드 서비스
 * - 팔로우한 토픽의 게시글 피드 생성
 * - 시간순 정렬, 페이징 지원
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicFeedService {

    private final BoardRepository boardRepository;
    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final UserTopicFollowRepository userTopicFollowRepository;
    private final BoardTopicRepository boardTopicRepository;
    private final TopicRepository topicRepository;

    /**
     * 토픽 피드 조회 (무한 스크롤용)
     *
     * @param userId 현재 사용자 ID
     * @param topicIds 특정 토픽만 필터링 (null이면 팔로우한 모든 토픽)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (기본 20)
     * @return 토픽 피드 아이템 리스트
     */
    public List<TopicFeedItemDTO> getTopicFeed(Long userId, List<Long> topicIds, int page, int size) {
        // 1. 타겟 토픽 ID 결정
        List<Long> targetTopicIds = topicIds != null && !topicIds.isEmpty()
            ? topicIds
            : userTopicFollowRepository.findTopicIdsByUserId(userId);

        if (targetTopicIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 각 토픽의 게시글 ID 수집 (중복 제거)
        Set<Long> boardIds = new HashSet<>();
        for (Long topicId : targetTopicIds) {
            List<Long> ids = boardTopicRepository.findBoardIdsByTopicId(topicId);
            boardIds.addAll(ids);
        }

        if (boardIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 게시글 조회 (과다 조회 후 페이징)
        int fetchSize = Math.min((page + 1) * size * 2, 1000); // 최대 1000개까지만
        Pageable pageable = PageRequest.of(0, fetchSize);

        List<Board> boards = boardRepository
            .findByIdInAndIsDraftFalseOrderByCreatedAtDesc(
                new ArrayList<>(boardIds),
                pageable
            ).getContent();

        // 4. DTO 변환
        List<TopicFeedItemDTO> items = convertBoardsToFeedItems(boards);

        // 5. 시간순 정렬
        items.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        // 6. 페이징 처리
        int start = page * size;
        int end = Math.min(start + size, items.size());

        return start < items.size() ? items.subList(start, end) : Collections.emptyList();
    }

    /**
     * 단일 토픽의 피드 조회 (간편 메서드)
     */
    public List<TopicFeedItemDTO> getTopicFeedByTopicId(Long userId, Long topicId, int page, int size) {
        return getTopicFeed(userId, Collections.singletonList(topicId), page, size);
    }

    /**
     * 팔로우한 모든 토픽의 피드 조회 (간편 메서드)
     */
    public List<TopicFeedItemDTO> getAllFollowedTopicsFeed(Long userId, int page, int size) {
        return getTopicFeed(userId, null, page, size);
    }

    /**
     * Board 엔티티를 TopicFeedItemDTO로 변환
     */
    private List<TopicFeedItemDTO> convertBoardsToFeedItems(List<Board> boards) {
        return boards.stream().map(board -> {
            User author = userRepository.findById(board.getUserId()).orElse(null);

            // 게시글의 토픽 정보 조회
            List<TopicFeedItemDTO.TopicInfo> topicInfos = getTopicInfosForBoard(board.getId());

            return TopicFeedItemDTO.builder()
                .itemType("POST")
                .timestamp(board.getCreatedAt())
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .author(author != null ? author.getNickname() : "Unknown")
                .authorId(board.getUserId())
                .createdAt(board.getCreatedAt())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                .topics(topicInfos)
                .primaryTopic(topicInfos.isEmpty() ? null : topicInfos.get(0))
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * 게시글의 토픽 정보 조회
     */
    private List<TopicFeedItemDTO.TopicInfo> getTopicInfosForBoard(Long boardId) {
        List<Long> topicIds = boardTopicRepository.findTopicIdsByBoardId(boardId);

        return topicIds.stream()
            .map(topicId -> topicRepository.findById(topicId).orElse(null))
            .filter(Objects::nonNull)
            .filter(Topic::getEnabled)
            .map(topic -> TopicFeedItemDTO.TopicInfo.builder()
                .id(topic.getId())
                .name(topic.getName())
                .icon(topic.getIcon())
                .color(topic.getColor())
                .level(topic.getLevel())
                .fullPath(buildTopicPath(topic))
                .build())
            .collect(Collectors.toList());
    }

    /**
     * 토픽의 전체 경로 생성 (예: "개발 > Java > Spring")
     */
    private String buildTopicPath(Topic topic) {
        List<String> pathParts = new ArrayList<>();
        Topic current = topic;

        // 최대 깊이 제한 (무한 루프 방지)
        int maxDepth = 10;
        int depth = 0;

        while (current != null && depth < maxDepth) {
            pathParts.add(0, current.getName());

            if (current.getParentId() != null) {
                current = topicRepository.findById(current.getParentId()).orElse(null);
            } else {
                current = null;
            }
            depth++;
        }

        return String.join(" > ", pathParts);
    }

    /**
     * 피드에 표시할 총 아이템 수 추정 (페이징 UI용)
     */
    public long estimateFeedItemCount(Long userId, List<Long> topicIds) {
        List<Long> targetTopicIds = topicIds != null && !topicIds.isEmpty()
            ? topicIds
            : userTopicFollowRepository.findTopicIdsByUserId(userId);

        if (targetTopicIds.isEmpty()) {
            return 0;
        }

        // 각 토픽의 게시글 수 합산 (중복은 고려하지 않음, 근사치)
        long total = 0;
        for (Long topicId : targetTopicIds) {
            total += boardTopicRepository.countByTopicId(topicId);
        }

        return total;
    }
}

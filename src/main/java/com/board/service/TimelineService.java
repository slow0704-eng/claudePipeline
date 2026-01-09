package com.board.service;

import com.board.dto.TimelineItemDTO;
import com.board.entity.Board;
import com.board.entity.Follow;
import com.board.entity.Share;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.FollowRepository;
import com.board.repository.ShareRepository;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 타임라인 피드 서비스
 * - 팔로잉한 사용자들의 새 게시글 + 공유한 게시글을 시간순으로 혼합
 * - Facebook 뉴스피드 스타일
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineService {

    private final BoardRepository boardRepository;
    private final ShareRepository shareRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    /**
     * 타임라인 피드 조회 (무한 스크롤용)
     *
     * @param userId 현재 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (기본 20)
     * @return 타임라인 아이템 리스트
     */
    public List<TimelineItemDTO> getTimelineFeed(Long userId, int page, int size) {
        // 1. 팔로잉 사용자 ID 조회
        List<Long> followingIds = followRepository.findByFollowerId(userId)
            .stream()
            .map(Follow::getFollowingId)
            .collect(Collectors.toList());

        // 팔로우가 없으면 빈 결과 반환
        if (followingIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 게시글과 공유 조회 (더 많은 데이터 조회 후 병합)
        // 페이지별로 충분한 데이터를 확보하기 위해 더 많이 조회
        int fetchSize = (page + 1) * size * 2;
        Pageable pageable = PageRequest.of(0, fetchSize);

        List<Board> boards = boardRepository
            .findByUserIdInAndIsDraftFalseOrderByCreatedAtDesc(followingIds, pageable)
            .getContent();

        List<Share> shares = shareRepository
            .findByUserIdInOrderByCreatedAtDesc(followingIds, pageable)
            .getContent();

        // 3. DTO 변환
        List<TimelineItemDTO> allItems = new ArrayList<>();
        allItems.addAll(convertBoardsToTimelineItems(boards));
        allItems.addAll(convertSharesToTimelineItems(shares));

        // 4. 시간순 정렬
        allItems.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        // 5. 페이징 처리
        int start = page * size;
        int end = Math.min(start + size, allItems.size());

        return start < allItems.size() ? allItems.subList(start, end) : Collections.emptyList();
    }

    /**
     * Board 엔티티를 TimelineItemDTO로 변환
     */
    private List<TimelineItemDTO> convertBoardsToTimelineItems(List<Board> boards) {
        return boards.stream().map(board -> {
            User author = userRepository.findById(board.getUserId()).orElse(null);

            return TimelineItemDTO.builder()
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
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * Share 엔티티를 TimelineItemDTO로 변환
     */
    private List<TimelineItemDTO> convertSharesToTimelineItems(List<Share> shares) {
        return shares.stream().map(share -> {
            // 원본 게시글 조회
            Board board = boardRepository.findById(share.getBoardId()).orElse(null);
            if (board == null || board.getIsDraft()) {
                return null; // 임시저장이거나 삭제된 게시글은 제외
            }

            // 작성자와 공유자 정보 조회
            User author = userRepository.findById(board.getUserId()).orElse(null);
            User sharer = userRepository.findById(share.getUserId()).orElse(null);

            return TimelineItemDTO.builder()
                .itemType("SHARE")
                .timestamp(share.getCreatedAt()) // 공유 시간 기준 정렬
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .author(author != null ? author.getNickname() : "Unknown")
                .authorId(board.getUserId())
                .createdAt(board.getCreatedAt())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                // 공유 정보
                .shareId(share.getId())
                .shareType(share.getShareType().name())
                .quoteContent(share.getQuoteContent())
                .sharer(sharer != null ? sharer.getNickname() : "Unknown")
                .sharerId(share.getUserId())
                .sharedAt(share.getCreatedAt())
                .build();
        })
        .filter(item -> item != null) // null 제거
        .collect(Collectors.toList());
    }
}

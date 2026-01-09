package com.board.service;

import com.board.dto.TimelineItemDTO;
import com.board.entity.Board;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.FollowRepository;
import com.board.repository.LikeRepository;
import com.board.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 타임라인 서비스
 * 사용자의 타임라인 피드를 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimelineService {

    private final BoardRepository boardRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;

    /**
     * 사용자의 타임라인 피드 조회
     * 팔로우한 사용자들의 게시글을 최신순으로 반환
     *
     * @param userId 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 타임라인 아이템 목록
     */
    public List<TimelineItemDTO> getTimelineFeed(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 팔로우한 사용자 ID 목록 조회
        List<Long> followingUserIds = followRepository.findFollowingUserIds(userId);

        // 자신의 게시글도 포함
        followingUserIds.add(userId);

        // 팔로우한 사용자들의 게시글 조회
        org.springframework.data.domain.Page<Board> boardPage =
            boardRepository.findByUserIdInAndIsDraftFalseOrderByCreatedAtDesc(followingUserIds, pageable);

        // DTO로 변환
        return boardPage.getContent().stream()
                .map(board -> convertToTimelineItemDTO(board, userId))
                .collect(Collectors.toList());
    }

    /**
     * Board 엔티티를 TimelineItemDTO로 변환
     *
     * @param board 게시글 엔티티
     * @param currentUserId 현재 사용자 ID
     * @return 타임라인 아이템 DTO
     */
    private TimelineItemDTO convertToTimelineItemDTO(Board board, Long currentUserId) {
        User author = board.getUser();

        TimelineItemDTO dto = new TimelineItemDTO();
        dto.setId(board.getId());
        dto.setType("POST");
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setUserId(author.getId());
        dto.setUsername(author.getUsername());
        dto.setUserProfileImage(author.getProfileImage());
        dto.setCreatedAt(board.getCreatedAt());
        dto.setLikeCount(board.getLikeCount());
        dto.setCommentCount(board.getCommentCount());

        // 좋아요 여부 확인
        boolean isLiked = likeRepository.existsByUserIdAndTargetTypeAndTargetId(
                currentUserId, com.board.enums.TargetType.POST, board.getId());
        dto.setIsLiked(isLiked);

        // 북마크 여부 확인
        boolean isBookmarked = bookmarkRepository.existsByUserIdAndBoardId(currentUserId, board.getId());
        dto.setIsBookmarked(isBookmarked);

        return dto;
    }
}

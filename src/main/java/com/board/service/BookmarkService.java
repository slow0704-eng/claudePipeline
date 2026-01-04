package com.board.service;

import com.board.entity.Board;
import com.board.entity.Bookmark;
import com.board.repository.BoardRepository;
import com.board.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BoardRepository boardRepository;

    /**
     * 북마크 토글 (추가/제거)
     * @param userId 사용자 ID
     * @param boardId 게시글 ID
     * @return 북마크 상태 정보
     */
    @Transactional
    public Map<String, Object> toggleBookmark(Long userId, Long boardId) {
        Map<String, Object> result = new HashMap<>();

        // 이미 북마크되어 있는지 확인
        boolean exists = bookmarkRepository.existsByUserIdAndBoardId(userId, boardId);

        if (exists) {
            // 북마크 제거
            Bookmark bookmark = bookmarkRepository.findByUserIdAndBoardId(userId, boardId)
                    .orElseThrow(() -> new RuntimeException("북마크를 찾을 수 없습니다."));
            bookmarkRepository.delete(bookmark);
            result.put("bookmarked", false);
            result.put("message", "북마크가 해제되었습니다.");
        } else {
            // 북마크 추가
            Bookmark bookmark = new Bookmark();
            bookmark.setUserId(userId);
            bookmark.setBoardId(boardId);
            bookmarkRepository.save(bookmark);
            result.put("bookmarked", true);
            result.put("message", "북마크에 추가되었습니다.");
        }

        // 현재 북마크 수 반환
        long bookmarkCount = bookmarkRepository.countByBoardId(boardId);
        result.put("bookmarkCount", bookmarkCount);

        return result;
    }

    /**
     * 북마크 여부 확인
     * @param userId 사용자 ID
     * @param boardId 게시글 ID
     * @return 북마크 여부
     */
    public boolean isBookmarked(Long userId, Long boardId) {
        if (userId == null || boardId == null) {
            return false;
        }
        return bookmarkRepository.existsByUserIdAndBoardId(userId, boardId);
    }

    /**
     * 사용자의 북마크된 게시글 목록 조회
     * @param userId 사용자 ID
     * @return 북마크된 게시글 목록
     * N+1 문제 해결: findAllById를 사용하여 IN 쿼리로 일괄 조회
     */
    @Transactional(readOnly = true)
    public List<Board> getBookmarkedBoards(Long userId) {
        // 북마크된 게시글 ID 목록 조회
        List<Long> boardIds = bookmarkRepository.findBoardIdsByUserId(userId);

        if (boardIds.isEmpty()) {
            return List.of();
        }

        // N+1 방지: IN 쿼리로 한번에 조회
        return boardRepository.findAllById(boardIds);
    }

    /**
     * 특정 게시글의 북마크 수 조회
     * @param boardId 게시글 ID
     * @return 북마크 수
     */
    public long getBookmarkCount(Long boardId) {
        return bookmarkRepository.countByBoardId(boardId);
    }

    /**
     * 사용자의 총 북마크 수 조회
     * @param userId 사용자 ID
     * @return 북마크 수
     */
    public long getUserBookmarkCount(Long userId) {
        return bookmarkRepository.countByUserId(userId);
    }

    /**
     * 게시글 삭제 시 관련 북마크 삭제
     * @param boardId 게시글 ID
     */
    @Transactional
    public void deleteBookmarksByBoardId(Long boardId) {
        bookmarkRepository.deleteByBoardId(boardId);
    }
}

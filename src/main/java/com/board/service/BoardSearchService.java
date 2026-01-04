package com.board.service;

import com.board.entity.Board;
import com.board.enums.BoardStatus;
import com.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 게시글 검색 및 필터링 기능을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public Page<Board> searchBoardsWithFilters(
            BoardStatus status,
            Long categoryId,
            String keyword,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        return boardRepository.searchBoardsWithFilters(status, categoryId, keyword, startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Board> getBoardsByStatus(BoardStatus status, Pageable pageable) {
        return boardRepository.findByStatusOrderByCreatedAtDescTitleAsc(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Board> getBoardsByCategory(Long categoryId, Pageable pageable) {
        return boardRepository.findByCategoryIdOrderByCreatedAtDescTitleAsc(categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Board> getBoardsByStatusAndCategory(BoardStatus status, Long categoryId, Pageable pageable) {
        return boardRepository.findByStatusAndCategoryIdOrderByCreatedAtDescTitleAsc(status, categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Board> getAllBoardsForAdmin(Pageable pageable) {
        return boardRepository.findAllBoardsForAdmin(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Board> advancedSearch(
            String searchType,
            String keyword,
            Long categoryId,
            BoardStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean isPinned,
            Boolean isImportant,
            Pageable pageable
    ) {
        return boardRepository.advancedSearch(
                searchType, keyword, categoryId, status,
                startDate, endDate, isPinned, isImportant, pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<Board> simpleSearch(String keyword, Pageable pageable) {
        return boardRepository.searchByKeyword(keyword, pageable);
    }
}

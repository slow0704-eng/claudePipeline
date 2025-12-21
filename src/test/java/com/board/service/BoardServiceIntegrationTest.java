package com.board.service;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.enums.UserRole;
import com.board.repository.BoardRepository;
import com.board.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BoardServiceIntegrationTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear data
        boardRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setNickname("테스트유저");
        testUser.setRole(UserRole.MEMBER);
        testUser.setEnabled(true);
        testUser = userRepository.save(testUser);

        // Set authentication with UserDetails
        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(testUser.getUsername())
                        .password(testUser.getPassword())
                        .roles(testUser.getRole().name())
                        .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, testUser.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testCreateBoard() {
        // Given
        Board board = new Board();
        board.setTitle("테스트 제목");
        board.setContent("테스트 내용");

        // When
        Board savedBoard = boardService.createBoard(board);

        // Then
        assertNotNull(savedBoard);
        assertNotNull(savedBoard.getId());
        assertEquals("테스트 제목", savedBoard.getTitle());
        assertEquals("테스트 내용", savedBoard.getContent());
        assertEquals(testUser.getId(), savedBoard.getUserId());
        assertEquals(testUser.getNickname(), savedBoard.getNickname());
    }

    @Test
    void testGetAllBoards() {
        // Given
        Board board1 = new Board();
        board1.setTitle("제목1");
        board1.setContent("내용1");
        boardService.createBoard(board1);

        Board board2 = new Board();
        board2.setTitle("제목2");
        board2.setContent("내용2");
        boardService.createBoard(board2);

        // When
        List<Board> boards = boardService.getAllBoards();

        // Then
        assertEquals(2, boards.size());
    }

    @Test
    void testGetBoardById() {
        // Given
        Board board = new Board();
        board.setTitle("테스트 제목");
        board.setContent("테스트 내용");
        Board savedBoard = boardService.createBoard(board);

        // When
        Board foundBoard = boardService.getBoardById(savedBoard.getId());

        // Then
        assertNotNull(foundBoard);
        assertEquals(savedBoard.getId(), foundBoard.getId());
        assertEquals("테스트 제목", foundBoard.getTitle());
    }

    @Test
    void testGetBoardByIdNotFound() {
        // When & Then
        assertThrows(RuntimeException.class, () -> boardService.getBoardById(999L));
    }

    @Test
    void testIncreaseViewCount() {
        // Given
        Board board = new Board();
        board.setTitle("테스트 제목");
        board.setContent("테스트 내용");
        Board savedBoard = boardService.createBoard(board);
        int initialViewCount = savedBoard.getViewCount();

        // When
        Board updatedBoard = boardService.increaseViewCount(savedBoard.getId());

        // Then
        assertEquals(initialViewCount + 1, updatedBoard.getViewCount());
    }

    @Test
    void testUpdateBoard() {
        // Given
        Board board = new Board();
        board.setTitle("원래 제목");
        board.setContent("원래 내용");
        Board savedBoard = boardService.createBoard(board);

        // When
        Board updateDetails = new Board();
        updateDetails.setTitle("수정된 제목");
        updateDetails.setContent("수정된 내용");
        Board updatedBoard = boardService.updateBoard(savedBoard.getId(), updateDetails);

        // Then
        assertEquals("수정된 제목", updatedBoard.getTitle());
        assertEquals("수정된 내용", updatedBoard.getContent());
    }

    @Test
    void testDeleteBoard() {
        // Given
        Board board = new Board();
        board.setTitle("삭제할 게시글");
        board.setContent("삭제할 내용");
        Board savedBoard = boardService.createBoard(board);

        // When
        boardService.deleteBoard(savedBoard.getId());

        // Then
        assertThrows(RuntimeException.class, () -> boardService.getBoardById(savedBoard.getId()));
    }

    @Test
    void testIsOwner() {
        // Given
        Board board = new Board();
        board.setTitle("테스트 제목");
        board.setContent("테스트 내용");
        Board savedBoard = boardService.createBoard(board);

        // When
        boolean isOwner = boardService.isOwner(savedBoard, testUser);

        // Then
        assertTrue(isOwner);
    }

    @Test
    void testGetBoardsByUserId() {
        // Given
        Board board1 = new Board();
        board1.setTitle("제목1");
        board1.setContent("내용1");
        boardService.createBoard(board1);

        Board board2 = new Board();
        board2.setTitle("제목2");
        board2.setContent("내용2");
        boardService.createBoard(board2);

        // When
        List<Board> userBoards = boardService.getBoardsByUserId(testUser.getId());

        // Then
        assertEquals(2, userBoards.size());
    }
}

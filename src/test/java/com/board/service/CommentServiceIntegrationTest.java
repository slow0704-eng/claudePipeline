package com.board.service;

import com.board.entity.Board;
import com.board.entity.Comment;
import com.board.entity.User;
import com.board.enums.UserRole;
import com.board.repository.BoardRepository;
import com.board.repository.CommentRepository;
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
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Board testBoard;

    @BeforeEach
    void setUp() {
        // Clear data
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .nickname("테스트유저")
                .role(UserRole.MEMBER)
                .enabled(true)
                .build();
        testUser = userRepository.save(testUser);

        // Create test board
        testBoard = Board.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(testUser.getId())
                .nickname(testUser.getNickname())
                .author(testUser.getNickname())
                .build();
        testBoard = boardRepository.save(testBoard);

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
    void testCreateComment() {
        // When
        Comment comment = commentService.createComment(testBoard.getId(), "테스트 댓글", null);

        // Then
        assertNotNull(comment);
        assertNotNull(comment.getId());
        assertEquals("테스트 댓글", comment.getContent());
        assertEquals(testUser.getId(), comment.getUserId());
        assertEquals(testUser.getNickname(), comment.getNickname());
        assertEquals(testBoard.getId(), comment.getBoardId());
        assertNull(comment.getParentCommentId());
        assertFalse(comment.getIsDeleted());
    }

    @Test
    void testCreateCommentWithoutAuth() {
        // Given
        SecurityContextHolder.clearContext();

        // When & Then
        assertThrows(RuntimeException.class, () ->
                commentService.createComment(testBoard.getId(), "테스트 댓글", null)
        );
    }

    @Test
    void testCreateReply() {
        // Given
        Comment parentComment = commentService.createComment(testBoard.getId(), "부모 댓글", null);

        // When
        Comment reply = commentService.createComment(testBoard.getId(), "답글", parentComment.getId());

        // Then
        assertNotNull(reply);
        assertEquals("답글", reply.getContent());
        assertEquals(parentComment.getId(), reply.getParentCommentId());
    }

    @Test
    void testDeleteComment() {
        // Given
        Comment comment = commentService.createComment(testBoard.getId(), "삭제할 댓글", null);

        // When
        commentService.deleteComment(comment.getId());

        // Then
        Comment deletedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertTrue(deletedComment.getIsDeleted());
        assertEquals("[삭제된 댓글입니다]", deletedComment.getContent());
    }

    @Test
    void testDeleteCommentNotOwner() {
        // Given
        Comment comment = commentService.createComment(testBoard.getId(), "댓글", null);

        // Create another user
        User anotherUser = User.builder()
                .username("another")
                .password(passwordEncoder.encode("password"))
                .nickname("다른유저")
                .role(UserRole.MEMBER)
                .enabled(true)
                .build();
        anotherUser = userRepository.save(anotherUser);

        // Change authentication
        org.springframework.security.core.userdetails.UserDetails anotherUserDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(anotherUser.getUsername())
                        .password(anotherUser.getPassword())
                        .roles(anotherUser.getRole().name())
                        .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(anotherUserDetails, anotherUser.getPassword(), anotherUserDetails.getAuthorities())
        );

        // When & Then
        assertThrows(RuntimeException.class, () ->
                commentService.deleteComment(comment.getId())
        );
    }

    @Test
    void testGetCommentsTreeByBoardId() {
        // Given
        Comment parent1 = commentService.createComment(testBoard.getId(), "부모1", null);
        Comment parent2 = commentService.createComment(testBoard.getId(), "부모2", null);
        Comment reply1_1 = commentService.createComment(testBoard.getId(), "답글1-1", parent1.getId());
        Comment reply1_2 = commentService.createComment(testBoard.getId(), "답글1-2", parent1.getId());

        // When
        List<Comment> tree = commentService.getCommentsTreeByBoardId(testBoard.getId());

        // Then
        assertEquals(2, tree.size()); // 2 root comments
        assertEquals(2, tree.get(0).getReplies().size()); // parent1 has 2 replies
        assertEquals(0, tree.get(1).getReplies().size()); // parent2 has no replies
    }

    @Test
    void testGetCommentCountByBoardId() {
        // Given
        commentService.createComment(testBoard.getId(), "댓글1", null);
        commentService.createComment(testBoard.getId(), "댓글2", null);
        Comment comment3 = commentService.createComment(testBoard.getId(), "댓글3", null);
        commentService.deleteComment(comment3.getId());

        // When
        long count = commentService.getCommentCountByBoardId(testBoard.getId());

        // Then
        assertEquals(2, count); // 삭제된 댓글은 제외
    }

    @Test
    void testGetCommentsByUserId() {
        // Given
        commentService.createComment(testBoard.getId(), "댓글1", null);
        commentService.createComment(testBoard.getId(), "댓글2", null);

        // When
        List<Comment> comments = commentService.getCommentsByUserId(testUser.getId());

        // Then
        assertEquals(2, comments.size());
    }

    @Test
    void testIsOwner() {
        // Given
        Comment comment = commentService.createComment(testBoard.getId(), "댓글", null);

        // When
        boolean isOwner = commentService.isOwner(comment, testUser);

        // Then
        assertTrue(isOwner);
    }
}

package com.board.service;

import com.board.entity.Board;
import com.board.entity.Comment;
import com.board.entity.Like;
import com.board.entity.User;
import com.board.enums.TargetType;
import com.board.enums.UserRole;
import com.board.repository.BoardRepository;
import com.board.repository.CommentRepository;
import com.board.repository.LikeRepository;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LikeServiceIntegrationTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Board testBoard;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        // Clear data
        likeRepository.deleteAll();
        commentRepository.deleteAll();
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

        // Create test board
        testBoard = new Board();
        testBoard.setTitle("테스트 게시글");
        testBoard.setContent("테스트 내용");
        testBoard.setUserId(testUser.getId());
        testBoard.setNickname(testUser.getNickname());
        testBoard.setAuthor(testUser.getNickname());
        testBoard = boardRepository.save(testBoard);

        // Create test comment
        testComment = new Comment();
        testComment.setBoardId(testBoard.getId());
        testComment.setUserId(testUser.getId());
        testComment.setNickname(testUser.getNickname());
        testComment.setContent("테스트 댓글");
        testComment.setIsDeleted(false);
        testComment = commentRepository.save(testComment);

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
    void testToggleLikePost_AddLike() {
        // When
        Map<String, Object> result = likeService.toggleLike(TargetType.POST, testBoard.getId());

        // Then
        assertTrue((Boolean) result.get("isLiked"));
        assertEquals(1L, result.get("likeCount"));

        // Verify in database
        assertTrue(likeRepository.existsByUserIdAndTargetTypeAndTargetId(
                testUser.getId(), TargetType.POST, testBoard.getId()));
    }

    @Test
    void testToggleLikePost_RemoveLike() {
        // Given
        likeService.toggleLike(TargetType.POST, testBoard.getId());

        // When
        Map<String, Object> result = likeService.toggleLike(TargetType.POST, testBoard.getId());

        // Then
        assertFalse((Boolean) result.get("isLiked"));
        assertEquals(0L, result.get("likeCount"));

        // Verify in database
        assertFalse(likeRepository.existsByUserIdAndTargetTypeAndTargetId(
                testUser.getId(), TargetType.POST, testBoard.getId()));
    }

    @Test
    void testToggleLikeComment_AddLike() {
        // When
        Map<String, Object> result = likeService.toggleLike(TargetType.COMMENT, testComment.getId());

        // Then
        assertTrue((Boolean) result.get("isLiked"));
        assertEquals(1L, result.get("likeCount"));

        // Verify comment like count updated
        Comment updatedComment = commentRepository.findById(testComment.getId()).orElseThrow();
        assertEquals(1, updatedComment.getLikeCount());
    }

    @Test
    void testToggleLikeWithoutAuth() {
        // Given
        SecurityContextHolder.clearContext();

        // When & Then
        assertThrows(RuntimeException.class, () ->
                likeService.toggleLike(TargetType.POST, testBoard.getId())
        );
    }

    @Test
    void testIsLiked() {
        // Given
        likeService.toggleLike(TargetType.POST, testBoard.getId());

        // When
        boolean isLiked = likeService.isLiked(TargetType.POST, testBoard.getId(), testUser.getId());

        // Then
        assertTrue(isLiked);
    }

    @Test
    void testIsLiked_NotLiked() {
        // When
        boolean isLiked = likeService.isLiked(TargetType.POST, testBoard.getId(), testUser.getId());

        // Then
        assertFalse(isLiked);
    }

    @Test
    void testGetLikeCount() {
        // Given
        likeService.toggleLike(TargetType.POST, testBoard.getId());

        // Create another user and like
        User anotherUser = new User();
        anotherUser.setUsername("another");
        anotherUser.setPassword(passwordEncoder.encode("password"));
        anotherUser.setNickname("다른유저");
        anotherUser.setRole(UserRole.MEMBER);
        anotherUser.setEnabled(true);
        anotherUser = userRepository.save(anotherUser);

        org.springframework.security.core.userdetails.UserDetails anotherUserDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(anotherUser.getUsername())
                        .password(anotherUser.getPassword())
                        .roles(anotherUser.getRole().name())
                        .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(anotherUserDetails, anotherUser.getPassword(), anotherUserDetails.getAuthorities())
        );
        likeService.toggleLike(TargetType.POST, testBoard.getId());

        // When
        long count = likeService.getLikeCount(TargetType.POST, testBoard.getId());

        // Then
        assertEquals(2, count);
    }

    @Test
    void testGetLikedPostsByUserId() {
        // Given
        Board board2 = new Board();
        board2.setTitle("게시글2");
        board2.setContent("내용2");
        board2.setUserId(testUser.getId());
        board2.setNickname(testUser.getNickname());
        board2.setAuthor(testUser.getNickname());
        board2 = boardRepository.save(board2);

        likeService.toggleLike(TargetType.POST, testBoard.getId());
        likeService.toggleLike(TargetType.POST, board2.getId());

        // When
        List<Like> likedPosts = likeService.getLikedPostsByUserId(testUser.getId());

        // Then
        assertEquals(2, likedPosts.size());
    }

    @Test
    void testBoardLikeCountUpdate() {
        // When
        likeService.toggleLike(TargetType.POST, testBoard.getId());

        // Then
        Board updatedBoard = boardRepository.findById(testBoard.getId()).orElseThrow();
        assertEquals(1, updatedBoard.getLikeCount());
    }

    @Test
    void testMultipleUsersLikingSamePost() {
        // Given
        likeService.toggleLike(TargetType.POST, testBoard.getId());

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("password"));
        user2.setNickname("유저2");
        user2.setRole(UserRole.MEMBER);
        user2.setEnabled(true);
        user2 = userRepository.save(user2);

        org.springframework.security.core.userdetails.UserDetails user2Details =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user2.getUsername())
                        .password(user2.getPassword())
                        .roles(user2.getRole().name())
                        .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user2Details, user2.getPassword(), user2Details.getAuthorities())
        );

        // When
        Map<String, Object> result = likeService.toggleLike(TargetType.POST, testBoard.getId());

        // Then
        assertTrue((Boolean) result.get("isLiked"));
        assertEquals(2L, result.get("likeCount"));

        Board updatedBoard = boardRepository.findById(testBoard.getId()).orElseThrow();
        assertEquals(2, updatedBoard.getLikeCount());
    }
}

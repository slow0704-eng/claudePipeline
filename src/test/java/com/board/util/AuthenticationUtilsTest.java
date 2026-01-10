package com.board.util;

import com.board.entity.User;
import com.board.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * AuthenticationUtils 테스트
 */
public class AuthenticationUtilsTest {

    @Mock
    private UserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        // Clear security context
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        closeable.close();
    }

    @Test
    void getCurrentUser_인증된_사용자_조회_성공() {
        // Given
        String username = "testuser";
        User mockUser = User.builder()
                .id(1L)
                .username(username)
                .nickname("테스트유저")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                username, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findByUsername(username)).thenReturn(mockUser);

        // When
        User result = AuthenticationUtils.getCurrentUser(userService);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo(username);
    }

    @Test
    void getCurrentUser_인증되지_않은_경우_null_반환() {
        // Given - SecurityContext가 비어있음

        // When
        User result = AuthenticationUtils.getCurrentUser(userService);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void getCurrentUser_익명사용자인_경우_null_반환() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "anonymousUser", null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        User result = AuthenticationUtils.getCurrentUser(userService);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void getCurrentUserId_인증된_사용자_ID_조회_성공() {
        // Given
        String username = "testuser";
        User mockUser = User.builder()
                .id(123L)
                .username(username)
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                username, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findByUsername(username)).thenReturn(mockUser);

        // When
        Long result = AuthenticationUtils.getCurrentUserId(userService);

        // Then
        assertThat(result).isEqualTo(123L);
    }

    @Test
    void getCurrentUserId_인증되지_않은_경우_null_반환() {
        // Given - SecurityContext가 비어있음

        // When
        Long result = AuthenticationUtils.getCurrentUserId(userService);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void isAuthenticated_인증된_경우_true_반환() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "testuser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean result = AuthenticationUtils.isAuthenticated();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isAuthenticated_인증되지_않은_경우_false_반환() {
        // Given - SecurityContext가 비어있음

        // When
        boolean result = AuthenticationUtils.isAuthenticated();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isAuthenticated_익명사용자인_경우_false_반환() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "anonymousUser", null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean result = AuthenticationUtils.isAuthenticated();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void hasRole_ROLE_ADMIN_권한이_있는_경우_true_반환() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean result = AuthenticationUtils.hasRole("ROLE_ADMIN");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void hasRole_권한이_없는_경우_false_반환() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "member", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean result = AuthenticationUtils.hasRole("ROLE_ADMIN");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void hasRole_인증되지_않은_경우_false_반환() {
        // Given - SecurityContext가 비어있음

        // When
        boolean result = AuthenticationUtils.hasRole("ROLE_ADMIN");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isAdmin_ADMIN_권한이_있는_경우_true_반환() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean result = AuthenticationUtils.isAdmin();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isMember_MEMBER_권한이_있는_경우_true_반환() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "member", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean result = AuthenticationUtils.isMember();

        // Then
        assertThat(result).isTrue();
    }
}

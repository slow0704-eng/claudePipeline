package com.board.util;

import com.board.entity.User;
import com.board.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 인증 관련 유틸리티 클래스
 *
 * SecurityContext에서 현재 사용자 정보를 조회하는 헬퍼 메서드 제공
 * 기존 13개 파일에 중복되어 있던 getCurrentUser() 로직을 통합
 */
public class AuthenticationUtils {

    /**
     * 현재 인증된 사용자 정보를 조회합니다.
     *
     * @param userService 사용자 조회를 위한 서비스
     * @return 현재 사용자 엔티티, 인증되지 않은 경우 null
     */
    public static User getCurrentUser(UserService userService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String username = authentication.getName();
        return userService.findByUsername(username);
    }

    /**
     * 현재 인증된 사용자의 ID를 조회합니다.
     *
     * @param userService 사용자 조회를 위한 서비스
     * @return 현재 사용자 ID, 인증되지 않은 경우 null
     */
    public static Long getCurrentUserId(UserService userService) {
        User user = getCurrentUser(userService);
        return user != null ? user.getId() : null;
    }

    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     *
     * @return 인증된 경우 true, 그렇지 않으면 false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               !authentication.getPrincipal().equals("anonymousUser");
    }

    /**
     * 현재 사용자가 특정 권한을 가지고 있는지 확인합니다.
     *
     * @param role 확인할 권한 (예: "ROLE_ADMIN", "ROLE_MEMBER")
     * @return 권한이 있으면 true, 그렇지 않으면 false
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }

    /**
     * 현재 사용자가 ADMIN 권한을 가지고 있는지 확인합니다.
     *
     * @return ADMIN 권한이 있으면 true, 그렇지 않으면 false
     */
    public static boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * 현재 사용자가 MEMBER 권한을 가지고 있는지 확인합니다.
     *
     * @return MEMBER 권한이 있으면 true, 그렇지 않으면 false
     */
    public static boolean isMember() {
        return hasRole("ROLE_MEMBER");
    }
}

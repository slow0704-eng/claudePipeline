package com.board.service;

import com.board.entity.User;
import com.board.enums.UserRole;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String password, String nickname, String email, String name) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // Check if nickname already exists
        if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setEmail(email);
        user.setName(name);
        user.setRole(UserRole.MEMBER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        // Check if new nickname already exists
        if (userRepository.existsByNickname(newNickname)) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setNickname(newNickname);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean verifyPassword(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return passwordEncoder.matches(password, user.getPassword());
    }

    // Find username by email and name
    public String findUsernameByEmailAndName(String email, String name) {
        User user = userRepository.findByEmailAndName(email, name)
                .orElseThrow(() -> new RuntimeException("일치하는 사용자 정보가 없습니다."));
        return user.getUsername();
    }

    // Verify user for password reset
    public User verifyUserForPasswordReset(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new RuntimeException("일치하는 사용자 정보가 없습니다."));
    }

    // Reset password (without current password verification)
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 회원 탈퇴
     * - 개인정보보호법 준수: 식별 가능한 개인정보 즉시 삭제/익명화
     * - 통계용 비식별 정보만 유지 (id, role, createdAt, deletedAt, deleteReason)
     */
    @Transactional
    public void deleteUser(Long userId, String password, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이미 탈퇴한 사용자인지 확인
        if (!user.isEnabled()) {
            throw new RuntimeException("이미 탈퇴한 계정입니다.");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 개인정보 익명화 처리 (개인정보보호법 준수)
        LocalDateTime now = LocalDateTime.now();
        String anonymousId = "deleted_user_" + userId + "_" + System.currentTimeMillis();

        // 식별 가능한 개인정보 삭제/익명화
        user.setUsername(anonymousId);  // 아이디 익명화
        user.setPassword("");  // 비밀번호 삭제
        user.setNickname("탈퇴한사용자");  // 닉네임 익명화 (게시글에 표시)
        user.setEmail(null);  // 이메일 삭제
        user.setName(null);  // 이름 삭제

        // 계정 비활성화 및 탈퇴 정보 기록 (통계용 비식별 정보)
        user.setEnabled(false);
        user.setDeletedAt(now);
        user.setDeleteReason(reason);  // 서비스 개선용

        userRepository.save(user);
    }

    /**
     * 탈퇴한 회원인지 확인
     */
    public boolean isDeleted(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return !user.isEnabled() && user.getDeletedAt() != null;
    }

    /**
     * 탈퇴한 회원 복구 - 개인정보보호법으로 인해 불가능
     *
     * 개인정보가 이미 삭제/익명화되었으므로 복구할 수 없습니다.
     * 탈퇴 시 아이디, 비밀번호, 이메일, 이름이 즉시 삭제되어 복원 불가능합니다.
     *
     * @deprecated 개인정보보호법 준수를 위해 탈퇴한 회원은 복구할 수 없습니다.
     */
    @Deprecated
    @Transactional
    public void restoreUser(Long userId) {
        throw new UnsupportedOperationException(
            "개인정보보호법에 따라 탈퇴한 회원은 복구할 수 없습니다. " +
            "개인정보가 이미 삭제되었습니다."
        );
    }
}

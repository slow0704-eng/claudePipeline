package com.board.service;

import com.board.entity.User;
import com.board.enums.UserRole;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

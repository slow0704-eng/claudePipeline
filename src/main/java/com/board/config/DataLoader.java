package com.board.config;

import com.board.entity.User;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 애플리케이션 시작 시 기본 데이터를 로드하는 컴포넌트
 *
 * - 기본 관리자 계정 자동 생성
 * - 서버 재시작 시 매번 실행되지만, 이미 존재하는 계정은 중복 생성하지 않음
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdminAccount();
    }

    /**
     * 기본 관리자 계정 생성
     *
     * 계정 정보:
     * - 사용자명: admin
     * - 비밀번호: admin1234
     * - 닉네임: 시스템관리자
     * - 이메일: admin@board.com
     * - 권한: ROLE_ADMIN
     */
    private void createDefaultAdminAccount() {
        try {
            // 이미 admin 계정이 존재하는지 확인
            if (userRepository.existsByUsername("admin")) {
                log.info("✓ 관리자 계정이 이미 존재합니다. (username: admin)");
                return;
            }

            // 새 관리자 계정 생성
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setNickname("시스템관리자");
            admin.setEmail("admin@board.com");
            admin.setRole(com.board.enums.UserRole.ADMIN);
            admin.setCreatedAt(LocalDateTime.now());

            userRepository.save(admin);

            log.info("========================================");
            log.info("✓ 기본 관리자 계정이 생성되었습니다!");
            log.info("========================================");
            log.info("  사용자명: admin");
            log.info("  비밀번호: admin1234");
            log.info("  접속 URL: http://localhost:8080/login");
            log.info("  대시보드: http://localhost:8080/admin/dashboard");
            log.info("========================================");
            log.info("⚠ 보안을 위해 첫 로그인 후 반드시 비밀번호를 변경하세요!");
            log.info("========================================");

        } catch (Exception e) {
            log.error("관리자 계정 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}

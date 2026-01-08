package com.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final Environment environment;

    /**
     * Static 리소스는 Security 필터 체인에서 완전히 제외
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        boolean isDevEnvironment = Arrays.asList(environment.getActiveProfiles()).contains("dev");

        http
            // CSRF 설정
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")  // REST API는 CSRF 예외
            )
            .authorizeHttpRequests(auth -> {
                auth
                    // Static resources - MUST BE FIRST
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/static/**").permitAll()

                    // File serving - 게스트도 첨부파일을 볼 수 있어야 함
                    .requestMatchers("/files/**").permitAll()

                    // Authentication pages
                    .requestMatchers("/auth/login", "/auth/register", "/auth/find-username", "/auth/find-password", "/auth/reset-password").permitAll()

                    // Legal pages - 모든 사용자 접근 가능
                    .requestMatchers("/legal/**").permitAll();

                // Debug tools - 개발 환경에서만 허용
                if (isDevEnvironment) {
                    auth.requestMatchers("/debug/**").permitAll();
                }

                auth

                // API endpoints - public API
                .requestMatchers("/api/search/**").permitAll()
                .requestMatchers("/api/notifications/**").permitAll()
                .requestMatchers("/api/shares/info/**", "/api/shares/users/**").permitAll()
                .requestMatchers("/api/shares/track-external").permitAll()
                .requestMatchers("/api/shares/statistics/**").permitAll()
                .requestMatchers("/api/shares/ranking/**").permitAll()
                .requestMatchers("/api/shares/recent-external").permitAll()
                .requestMatchers("/api/hashtags/**").permitAll()
                .requestMatchers("/api/topics/explore/**").permitAll()
                .requestMatchers("/api/topics/*/follow-status", "/api/topics/*/followers").permitAll()
                .requestMatchers("/api/topics/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/messages/unread-count").permitAll()

                // Member only - must come before public /board patterns
                .requestMatchers("/board/new", "/board/*/edit").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/comments/**", "/likes/**", "/reports/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/messages/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/api/shares/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/mypage/**").hasAnyRole("MEMBER", "ADMIN")

                // Community - member only (must come before public /community patterns)
                .requestMatchers("/community/new", "/community/*/edit", "/community/*/delete").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/community/*/join", "/community/*/leave").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/community/*/members/**").hasAnyRole("MEMBER", "ADMIN")

                // Admin only
                .requestMatchers("/admin/**").hasRole("ADMIN")

                    // Public access (GUEST) - /board/{id} must come after /board/new
                    .requestMatchers("/", "/board", "/board/{id}", "/board/search").permitAll()
                    .requestMatchers("/hashtag/{name}", "/hashtag/analytics").permitAll()
                    .requestMatchers("/topics/explore", "/topics/**").permitAll()

                    // Community - public access (must come after member-only /community patterns)
                    .requestMatchers("/community", "/community/{id}").permitAll()

                    .anyRequest().authenticated();
            })
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/board", true)
                .failureUrl("/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/board")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Authentication pages - must be first
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // Member only - must come before public /board patterns
                .requestMatchers("/board/new", "/board/*/edit").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/comments/**", "/likes/**", "/reports/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/mypage/**").hasAnyRole("MEMBER", "ADMIN")

                // Admin only
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Public access (GUEST) - /board/{id} must come after /board/new
                .requestMatchers("/", "/board", "/board/{id}", "/board/search").permitAll()

                .anyRequest().authenticated()
            )
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

package com.board.controller;

import com.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("message", "로그아웃되었습니다.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String passwordConfirm,
                          @RequestParam String nickname,
                          @RequestParam String email,
                          @RequestParam(required = false) String name,
                          Model model) {
        try {
            // Validate password confirmation
            if (!password.equals(passwordConfirm)) {
                model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
                model.addAttribute("username", username);
                model.addAttribute("nickname", nickname);
                model.addAttribute("email", email);
                model.addAttribute("name", name);
                return "auth/register";
            }

            // Validate minimum length
            if (username.length() < 4 || password.length() < 4) {
                model.addAttribute("error", "아이디와 비밀번호는 최소 4자 이상이어야 합니다.");
                model.addAttribute("username", username);
                model.addAttribute("nickname", nickname);
                model.addAttribute("email", email);
                model.addAttribute("name", name);
                return "auth/register";
            }

            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                model.addAttribute("error", "올바른 이메일 형식이 아닙니다.");
                model.addAttribute("username", username);
                model.addAttribute("nickname", nickname);
                model.addAttribute("email", email);
                model.addAttribute("name", name);
                return "auth/register";
            }

            // Register user
            userService.registerUser(username, password, nickname, email, name);

            return "redirect:/auth/login?registered=true";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("nickname", nickname);
            model.addAttribute("email", email);
            model.addAttribute("name", name);
            return "auth/register";
        }
    }

    @GetMapping("/find-username")
    public String findUsernamePage() {
        return "auth/find-username";
    }

    @PostMapping("/find-username")
    public String findUsername(@RequestParam String email,
                              @RequestParam String name,
                              Model model) {
        try {
            String username = userService.findUsernameByEmailAndName(email, name);
            model.addAttribute("foundUsername", username);
            return "auth/find-username-result";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("name", name);
            return "auth/find-username";
        }
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "auth/find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(@RequestParam String username,
                              @RequestParam String email,
                              Model model) {
        try {
            userService.verifyUserForPasswordReset(username, email);
            model.addAttribute("username", username);
            return "auth/reset-password";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "auth/find-password";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String username,
                               @RequestParam String newPassword,
                               @RequestParam String newPasswordConfirm,
                               Model model) {
        try {
            if (!newPassword.equals(newPasswordConfirm)) {
                model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
                model.addAttribute("username", username);
                return "auth/reset-password";
            }

            if (newPassword.length() < 4) {
                model.addAttribute("error", "비밀번호는 최소 4자 이상이어야 합니다.");
                model.addAttribute("username", username);
                return "auth/reset-password";
            }

            com.board.entity.User user = userService.findByUsername(username);
            userService.resetPassword(user.getId(), newPassword);

            model.addAttribute("message", "비밀번호가 재설정되었습니다. 로그인해주세요.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "auth/reset-password";
        }
    }
}

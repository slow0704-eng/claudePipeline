package com.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/legal")
public class LegalController {

    /**
     * 이용약관 페이지
     */
    @GetMapping("/terms")
    public String terms() {
        return "legal/terms";
    }

    /**
     * 개인정보처리방침 페이지
     */
    @GetMapping("/privacy")
    public String privacy() {
        return "legal/privacy";
    }

    /**
     * 운영정책 페이지
     */
    @GetMapping("/policy")
    public String policy() {
        return "legal/policy";
    }
}

package com.pharmquest.pharmquest.domain.user.web.controller;

import com.pharmquest.pharmquest.global.apiPayload.exception.handler.TempLoginHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TempLoginController {

    private final TempLoginHandler tempLoginHandler;

    @GetMapping("/tempLogin")
    public String login() {
        return "login";
    }

    @PostMapping("/tempLogin") // login.html 에서 name 가져와서 처리
    public void login(HttpServletResponse response, @ModelAttribute("name") String name) throws IOException {
        if (name.trim().isEmpty()) {
            name = "Unknown";
        }
        tempLoginHandler.tempLogin(response, name);
    }

}

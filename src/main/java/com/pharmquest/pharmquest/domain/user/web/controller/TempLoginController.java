package com.pharmquest.pharmquest.domain.user.web.controller;

import com.pharmquest.pharmquest.global.apiPayload.exception.handler.TempLoginHandler;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TempLoginController {

    private final TempLoginHandler tempLoginHandler;

    @PostMapping("/tempLogin")
    @Operation(summary = "임시 로그인 API" , description = "아무 이름이나 입력하면 로그인 됩니다.\n만약 가입된 이름이 있다면, 이미 있는 계정으로 로그인됩니다." +
            "\n만약 이름을 입력하지 않았다면 이름이 \"Unknown\"으로 적용됩니다.")
    public void login(HttpServletResponse response, @RequestParam(value = "name", required = false) String name) throws IOException {
        if (name == null ||  name.trim().isEmpty()) {
            name = "Unknown";
        }
        tempLoginHandler.tempLogin(response, name);
    }

}

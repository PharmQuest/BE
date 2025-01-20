package com.pharmquest.pharmquest.domain.token.web.controller;



import com.pharmquest.pharmquest.domain.token.service.TokenService;
import com.pharmquest.pharmquest.domain.token.web.dto.TokenResponseDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TokenController {
    @Qualifier("tokenServiceImpl")
    private final TokenService authService;


    // 액세스 토큰을 재발행하는 API
    @GetMapping("/reissue/access-token")
    @Operation(summary = "액세스 토큰 재발행 API")
    public ResponseEntity<ApiResponse<Object>> reissueAccessToken(
            @RequestHeader("Authorization") String authorizationHeader) {

        TokenResponseDTO accessToken = authService.reissueAccessToken(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._CREATED_ACCESS_TOKEN, accessToken);
    }
}
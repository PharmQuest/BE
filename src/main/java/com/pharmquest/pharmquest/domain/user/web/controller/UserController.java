package com.pharmquest.pharmquest.domain.user.web.controller;

import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.service.UserService;
import com.pharmquest.pharmquest.domain.user.web.dto.UserDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @GetMapping("detail")
    @Operation(summary = "유저 정보 반환 API")
    public ApiResponse<UserDTO.UserResponseDto> getScrapedSupplements(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        UserDTO.UserResponseDto userDetail = userService.getUser(user.getId());
        return ApiResponse.onSuccess(userDetail);
    }
}

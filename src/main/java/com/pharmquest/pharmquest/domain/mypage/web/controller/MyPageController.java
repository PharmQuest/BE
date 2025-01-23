package com.pharmquest.pharmquest.domain.mypage.web.controller;

import com.pharmquest.pharmquest.domain.mypage.converter.MyPageConverter;
import com.pharmquest.pharmquest.domain.mypage.service.MyPageService;
import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final JwtUtil jwtUtil;
    private final MyPageService myPageService;

    @GetMapping("/supplements")
    public List<MyPageResponseDTO.SupplementsResponseDto> searchMedicines(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        List<Supplements> supplements = myPageService.getScrapSupplements(user.getId());
        return ApiResponse.onSuccess(MyPageConverter.toSupplementsDto(supplements)).getResult();

    }
}

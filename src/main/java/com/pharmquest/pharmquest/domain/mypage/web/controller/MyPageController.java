package com.pharmquest.pharmquest.domain.mypage.web.controller;

import com.pharmquest.pharmquest.domain.mypage.converter.MyPageConverter;
import com.pharmquest.pharmquest.domain.mypage.service.MyPageService;
import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "스크랩한 영양제 조회 API")
    public ApiResponse<List<MyPageResponseDTO.SupplementsResponseDto>> getScrapedSupplements(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        List<MyPageResponseDTO.SupplementsResponseDto> supplements = myPageService.getScrapSupplements(user.getId());

        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/pharmacy")
    @Operation(summary = "스크랩한 약국 조회 API")
    public ApiResponse<MyPageResponseDTO.PharmacyResponse> getScrapedPharmacy(
            @RequestHeader(value = "Authorization") String authorizationHeader, @RequestParam("country") String country
    ) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        List<MyPageResponseDTO.PharmacyDto> pharmacies = myPageService.getScrapPharmacies(user, country.trim());
        return ApiResponse.of(SuccessStatus.MY_PAGE_PHARMACY, MyPageConverter.toPharmaciesDto(pharmacies));
    }
}

package com.pharmquest.pharmquest.domain.mypage.web.controller;

import com.pharmquest.pharmquest.domain.mypage.converter.MyPageConverter;
import com.pharmquest.pharmquest.domain.mypage.service.MyPageService;
import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final JwtUtil jwtUtil;
    private final MyPageService myPageService;

    @GetMapping("/supplements")
    @Operation(summary = "스크랩한 영양제 조회 API")
    public ApiResponse<Page<MyPageResponseDTO.SupplementsResponseDto>> getScrapedSupplements(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "카테고리") @RequestParam(defaultValue = "전체") CategoryKeyword category) {

        Pageable pageable = PageRequest.of(page, 20);
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        Page<MyPageResponseDTO.SupplementsResponseDto> supplements = myPageService.getScrapSupplements(user.getId(),pageable, category);

        return ApiResponse.onSuccess(supplements);
    }

    @GetMapping("/pharmacy")
    @Operation(summary = "스크랩한 약국 조회 API")
    public ApiResponse<MyPageResponseDTO.PharmacyResponse> getScrapedPharmacy(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("country") String country,
            @RequestParam(defaultValue = "1", value = "page") Integer page
    ) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Page<MyPageResponseDTO.PharmacyDto> pharmacies = myPageService.getScrapPharmacies(user, country, page);
        return ApiResponse.of(SuccessStatus.MY_PAGE_PHARMACY, MyPageConverter.toPharmaciesResponse(pharmacies));
    }

    @GetMapping("/myactivities/scrap")
    @Operation(summary = "나의활동 - 스크랩한 게시물 조회 API")
    public ApiResponse<Page<MyPageResponseDTO.ScrapPostResponseDTO>> getScrapedPost(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Page<MyPageResponseDTO.ScrapPostResponseDTO> scrapedPost = myPageService.getScrapPosts(user.getId(), pageable);
        return ApiResponse.onSuccess(scrapedPost);
    }
}

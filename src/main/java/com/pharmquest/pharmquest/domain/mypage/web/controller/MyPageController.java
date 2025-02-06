package com.pharmquest.pharmquest.domain.mypage.web.controller;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.mypage.converter.MyPageConverter;
import com.pharmquest.pharmquest.domain.mypage.service.MyPageService;
import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
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
import org.springframework.data.web.PageableDefault;
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
    public ApiResponse<Page<MyPageResponseDTO.SupplementsResponseDto>> getScrapedSupplements(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "카테고리") @RequestParam(defaultValue = "전체") CategoryKeyword category) {

        Pageable pageable = PageRequest.of(page-1, 20);
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        Page<MyPageResponseDTO.SupplementsResponseDto> supplements = myPageService.getScrapSupplements(user.getId(),pageable, category);

        return ApiResponse.onSuccess(supplements);
    }

    @GetMapping("/medicine")
    @Operation(summary = "스크랩한 상비약 조회 API")
    public ApiResponse<Page<MyPageResponseDTO.MedicineResponseDto>> getScrappedMedicines(
                                                            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                                            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
                                                            @Parameter(description = "카테고리: 미국, 한국, 전체") String country) {

        Pageable pageable = PageRequest.of(page-1, 10);
        Long userId = jwtUtil.getUserFromHeader(authorizationHeader).getId();

        Page<MyPageResponseDTO.MedicineResponseDto> scrappedMedicines = myPageService.getScrapMedicines(userId, pageable, country);
        return ApiResponse.onSuccess(scrappedMedicines);
    }

    @GetMapping("/pharmacy")
    @Operation(summary = "스크랩한 약국 조회 API")
    public ApiResponse<MyPageResponseDTO.PharmacyResponse> getScrapedPharmacy(
            @Parameter (hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestParam("country")PharmacyCountry country,
            @RequestParam(defaultValue = "1", value = "page") Integer page,
            @RequestParam(defaultValue = "1", value = "size") Integer size
    ) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Page<MyPageResponseDTO.PharmacyDto> pharmacies = myPageService.getScrapPharmacies(user, country, page, size);
        return ApiResponse.of(SuccessStatus.MY_PAGE_PHARMACY, MyPageConverter.toPharmaciesResponse(pharmacies, page, size));
    }

    @GetMapping("/activities/scrap")
    @Operation(summary = "나의활동 - 스크랩한 게시물 조회 API")
    public ApiResponse<Page<MyPageResponseDTO.ScrapPostResponseDTO>> getScrapedPost(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of(page-1, 10);
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Page<MyPageResponseDTO.ScrapPostResponseDTO> Post = myPageService.getScrapPosts(user.getId(), pageable);
        return ApiResponse.onSuccess(Post);
    }

    @GetMapping("/activities/comments")
    @Operation(summary = "나의활동 - 내가 작성한 댓글 조회 API")
    public ApiResponse<Page<MyPageResponseDTO.CommentResponseDTO>> getMyComments(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of(page-1, 5);
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Page<MyPageResponseDTO.CommentResponseDTO> myComments = myPageService.getMyComments(user.getId(), pageable);
        return ApiResponse.onSuccess(myComments);
    }

    @GetMapping("/activities/post")
    @Operation(summary = "나의활동 - 내가 작성한 게시물 조회 API")
    public ApiResponse<Page<MyPageResponseDTO.PostResponseDTO>> getMyPost(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of(page-1, 10);
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Page<MyPageResponseDTO.PostResponseDTO> myPost = myPageService.getMyPosts(user.getId(), pageable);
        return ApiResponse.onSuccess(myPost);
    }

    @GetMapping("/activities/notification")
    @Operation(summary = "나의활동 - 알림 조회 API")
    public ApiResponse<Page<MyPageResponseDTO.notificationResponseDTO>> getNotifications(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of(page-1, 5);
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Page<MyPageResponseDTO.notificationResponseDTO> notification = myPageService.getNotification(user.getId(), pageable);
        return ApiResponse.onSuccess(notification);
    }
}

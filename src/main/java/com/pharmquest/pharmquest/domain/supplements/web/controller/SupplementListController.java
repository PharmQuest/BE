package com.pharmquest.pharmquest.domain.supplements.web.controller;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import com.pharmquest.pharmquest.domain.supplements.service.Supplements.SupplementsService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "해외 인기 영양제", description = "해외 인기 영양제")
@RestController
@RequestMapping("/supplements")
@RequiredArgsConstructor
public class SupplementListController {

    private final SupplementsService supplementsService;
    private final JwtUtil jwtUtil;
    private final SupplementsRepository supplementsRepository;

    @GetMapping("/lists")
    @Operation(summary = "영양제 조회 API", description = "영양제 목록 조회 및 카테고리 필터링")
    public ApiResponse<List<SupplementsResponseDTO.SupplementsDto>> getSupplements(
            @Parameter(description = "카테고리") @RequestParam(required = false) CategoryKeyword category,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "사용자 ID", hidden = true) @RequestHeader(value = "authorization", required = false) String authorizationHeader
    ) {
        Long userId = null;
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            userId = jwtUtil.getUserFromHeader(authorizationHeader).getId();
        }
        Pageable pageable = PageRequest.of(page, 20);

        if (page == 0 && supplementsRepository.count() == 0) {
            supplementsService.saveSupplements();
        }

        Page<SupplementsResponseDTO.SupplementsDto> supplementsPage = supplementsService.getSupplements(category, pageable, userId);

        return ApiResponse.onSuccess(supplementsPage.getContent());
    }

    @GetMapping("/search")
    @Operation(summary = "영양제 검색 API", description = "영양제 검색")
    public ApiResponse<List<SupplementsResponseDTO.SupplementsSearchResponseDto>> searchSupplements(
            @Parameter(description = "키워드", hidden = true) @RequestHeader(value = "authorization", required = false) String authorizationHeader,
            @RequestParam(required = false) String keyword,
            @Parameter(description = "국가",schema = @Schema(allowableValues = {"USA", "KOREA"}))
            @RequestParam(required = false) Country country,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page
    ) {
        Long userId = null;

        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            userId = jwtUtil.getUserFromHeader(authorizationHeader).getId();
        }

        Pageable pageable = PageRequest.of(page, 20);

        Page<SupplementsResponseDTO.SupplementsSearchResponseDto> supplementsPage =
                supplementsService.searchSupplements(keyword, country, pageable, userId);

        return ApiResponse.onSuccess(supplementsPage.getContent());
    }

    @GetMapping("/med")
    @Operation(summary = "영양제 상세정보 조회 API", description = "영양제 상세정보 조회")
    public ApiResponse<SupplementsResponseDTO.SupplementsDetailResponseDto> SupplementsDetail(
            @Parameter(description = "영양제 ID", hidden = true) @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(name = "supplement_id") Long id
    ) {
        Long userId = null;
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            userId = jwtUtil.getUserFromHeader(authorizationHeader).getId();
        }
//        if (authroizationHeader == null || authroizationHeader.isEmpty()) {
//            throw new CommonExceptionHandler(ErrorStatus.AUTHORIZATION_HEADER_NOT_FOUND);
//        }

        SupplementsResponseDTO.SupplementsDetailResponseDto detailResponseDto =
                supplementsService.getSupplementById(id, userId);

        return ApiResponse.onSuccess(detailResponseDto);
    }
}
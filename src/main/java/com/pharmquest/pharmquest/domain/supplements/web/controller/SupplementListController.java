package com.pharmquest.pharmquest.domain.supplements.web.controller;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.Category;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.service.Supplements.SupplementsService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "해외 인기 영양제", description = "해외 인기 영양제")
@RestController
@RequestMapping("/supplements")
@RequiredArgsConstructor
public class SupplementListController {

    private final SupplementsService supplementsService;

    @GetMapping("/lists")
    @Operation(summary = "영양제 조회 API", description = "영양제 목록 조회 및 카테고리 필터링")
    public ApiResponse<List<SupplementsResponseDTO.SupplementsDto>> getSupplements(
            @Parameter(description = "카테고리") @RequestParam(required = false) CategoryKeyword category,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "사용자 ID") @RequestParam(required = false) Long userId
    ) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<SupplementsResponseDTO.SupplementsDto> supplementsPage = supplementsService.getSupplements(category, pageable, userId);

        if (page == 0 && supplementsPage.getTotalElements() == 0) {
            supplementsService.saveSupplements();
            supplementsPage = supplementsService.getSupplements(category, pageable, userId);
        }

        return ApiResponse.onSuccess(supplementsPage.getContent());
    }

    @GetMapping("/search")
    @Operation(summary = "영양제 검색 API", description = "영양제 검색")
    public ApiResponse<List<SupplementsResponseDTO.SupplementsSearchResponseDto>> searchSupplements(
            @Parameter(description = "키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "국가",schema = @Schema(allowableValues = {"USA", "JAPAN", "CHINA"}))
            @RequestParam(required = false) Country country,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "사용자 ID") @RequestParam(required = false) Long userId
    ) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<SupplementsResponseDTO.SupplementsSearchResponseDto> supplementsPage =
                supplementsService.searchSupplements(keyword, country, pageable, userId);

        return ApiResponse.onSuccess(supplementsPage.getContent());
    }

    @GetMapping("/med")
    @Operation(summary = "영양제 상세정보 조회 API", description = "영양제 상세정보 조회")
    public ApiResponse<SupplementsResponseDTO.SupplementsDetailResponseDto> SupplementsDetail(
            @Parameter(description = "영양제 ID") @RequestParam(required = true) Long id,
            @Parameter(description = "사용자 ID") @RequestParam(required = false) Long userId
    ) {
        SupplementsResponseDTO.SupplementsDetailResponseDto detailResponseDto =
                supplementsService.getSupplementById(id, userId);

        return ApiResponse.onSuccess(detailResponseDto);
    }
}
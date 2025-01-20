package com.pharmquest.pharmquest.domain.supplements.web.controller;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.enums.Nation;
import com.pharmquest.pharmquest.domain.supplements.service.SupplementsService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "해외 인기 영양제", description = "해외 인기 영양제")
@RestController
@RequestMapping("/api/v1/supplements")
@RequiredArgsConstructor
public class SupplementListController {

    private final SupplementsService supplementsService;

    @GetMapping("/lists")
    @Operation(summary = "영양제 조회 API", description = "영양제 목록 조회 및 카테고리 필터링")
    public ApiResponse<List<SupplementsResponseDTO.SupplementsDto>> getSupplements(
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<Supplements> supplementsPage = supplementsService.getSupplements(category, pageable);

        if (page == 0 && supplementsPage.getTotalElements() == 0) {
            supplementsService.saveSupplements();
            supplementsPage = supplementsService.getSupplements(category, pageable);
        }

        List<SupplementsResponseDTO.SupplementsDto> supplementsDtoList = supplementsPage.getContent()
                .stream()
                .map(supplement -> SupplementsResponseDTO.SupplementsDto.builder()
                        .name(supplement.getName())
                        .image(supplement.getImage())
                        .brand(supplement.getBrand())
                        .scrapCount(supplement.getScrapCount())
                        .category4(supplement.getCategory4())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(supplementsDtoList);
    }

    @GetMapping("/search")
    @Operation(summary = "영양제 검색 API", description = "영양제 검색")
    public ApiResponse<List<SupplementsResponseDTO.SupplementsSearchResponseDto>> searchSupplements(
            @Parameter(description = "키워드") @RequestParam(required = true) String keyword,
            @Parameter(description = "국가") @RequestParam(required = false) String nation,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page
    ){
        Pageable pageable = PageRequest.of(page, 10);
        Nation nationEnum = nation != null ? Nation.valueOf(nation.toUpperCase()) : null;

        Page<Supplements> supplementsPage = supplementsService.searchSupplements(keyword, nationEnum, pageable);

        List<SupplementsResponseDTO.SupplementsSearchResponseDto> supplementsSearchResponseDtoList = supplementsPage.getContent()
                .stream()
                .map(supplements -> SupplementsResponseDTO.SupplementsSearchResponseDto.builder()
                        .name(supplements.getName())
                        .image(supplements.getImage())
                        .brand(supplements.getBrand())
                        .category4(supplements.getCategory4())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(supplementsSearchResponseDtoList);
    }

    @GetMapping("/med")
    @Operation(summary = "영양제 상세정보 조회 API", description = "영양제 상세정보 조회")
    public ApiResponse<SupplementsResponseDTO.SupplementsDetailResponseDto> SupplementsDetail(
            @Parameter(description = "영양제 ID") @RequestParam(required = true) Long id
    ){
        Supplements supplement = supplementsService.getSupplementById(id);
        SupplementsResponseDTO.SupplementsDetailResponseDto detailResponseDto =
                SupplementsResponseDTO.SupplementsDetailResponseDto.builder()
                        .name(supplement.getName())
                        .image(supplement.getImage())
                        .brand(supplement.getBrand())
                        .maker(supplement.getMaker())
                        .link(supplement.getLink())
                        .isScrapped(supplement.getIsScrapped())
                        .scrapCount(supplement.getScrapCount())
                        .category1(supplement.getCategory1())
                        .category2(supplement.getCategory2())
                        .category3(supplement.getCategory3())
                        .category4(supplement.getCategory4())
                        .build();
        return ApiResponse.onSuccess(detailResponseDto);
    }
}
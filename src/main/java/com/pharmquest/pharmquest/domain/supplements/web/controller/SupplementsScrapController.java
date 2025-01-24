package com.pharmquest.pharmquest.domain.supplements.web.controller;

import com.pharmquest.pharmquest.domain.supplements.service.SupplementsScrap.SupplementsScrapService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsScrapResponseDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "영양제 스크랩", description = "영양제 스크랩")
@RestController
@RequestMapping("/supplements")
@RequiredArgsConstructor
public class SupplementsScrapController {
    private final SupplementsScrapService supplementsScrapService;

    @PatchMapping("/{supplementId}/scrap")
    @Operation(summary = "영양제 스크랩 API", description = "영양제 스크랩 변경(스크랩 추가)")
    public ApiResponse<SupplementsScrapResponseDTO> scrapSupplement(
            @Parameter(description = "영양제 ID") @PathVariable Long supplementId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId
    ) {
        SupplementsScrapResponseDTO response = supplementsScrapService.changeScrap(supplementId, userId);
        return ApiResponse.onSuccess(response);
    }
}
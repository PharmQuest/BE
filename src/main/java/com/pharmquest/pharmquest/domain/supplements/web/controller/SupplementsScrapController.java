package com.pharmquest.pharmquest.domain.supplements.web.controller;

import com.pharmquest.pharmquest.domain.supplements.service.SupplementsScrap.SupplementsScrapService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsScrapResponseDTO;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
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
    private final JwtUtil jwtUtil;

    @PatchMapping("/{supplementId}/scrap")
    @Operation(summary = "영양제 스크랩 API", description = "영양제 스크랩 변경(스크랩 추가)")
    public ApiResponse<SupplementsScrapResponseDTO> scrapSupplement(
            @Parameter(description = "영양제 ID", hidden = true) @RequestHeader(value = "authorization", required = false) String authorizationHeader,
            @PathVariable Long supplementId
    ) {
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new CommonExceptionHandler(ErrorStatus.AUTHORIZATION_HEADER_NOT_FOUND);
        }
        Long userId = jwtUtil.getUserFromHeader(authorizationHeader).getId();
        SupplementsScrapResponseDTO response = supplementsScrapService.changeScrap(supplementId, userId);
        return ApiResponse.onSuccess(response);
    }
}
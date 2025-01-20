package com.pharmquest.pharmquest.domain.pharmacy.web.controller;

import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyCommandService;
import com.pharmquest.pharmquest.domain.pharmacy.web.dto.PharmacyRequestDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pharmacy")
public class PharmacyController {

    private final PharmacyCommandService pharmacyCommandService;

    @PostMapping("/")
    @Operation(summary = "약국 스크랩", description = "memberId와 약국의 placeId로 약국을 스크랩합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PHARMACY201", description = "약국을 마이페이지에 성공적으로 스크랩했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "유저를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PHARMACY4001", description = "약국의 place_id가 올바르지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<Void> scrapPharmacy(@RequestBody @Valid PharmacyRequestDTO.ScrapDto request){
        pharmacyCommandService.scrapPharmacy(request.getUserId(), request.getPlaceId());
        return ApiResponse.noResultSuccess(SuccessStatus.PHARMACY_SCRAP);
    }

}

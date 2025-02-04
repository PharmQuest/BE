package com.pharmquest.pharmquest.domain.pharmacy.web.controller;

import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyCommandService;
import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyQueryService;
import com.pharmquest.pharmquest.domain.pharmacy.web.dto.PharmacyResponseDTO;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pharmacy")
public class PharmacyController {

    private final PharmacyCommandService pharmacyCommandService;
    private final PharmacyQueryService pharmacyQueryService;
    private final JwtUtil jwtUtil;

    @PatchMapping("/{place_id}")
    @Operation(summary = "약국 스크랩 API", description = "memberId와 약국의 placeId로 약국을 스크랩합니다.<br>만약 이미 저장된 약국을 저장하려고 할 경우 스크랩이 해제됩니다.<br>결과값은 없습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PHARMACY201", description = "약국을 마이페이지에 성공적으로 스크랩했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PHARMACY202", description = "약국을 스크랩을 해제했습니다."),
    })
    public ApiResponse<Void> scrapPharmacy(@Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader
                                          ,@PathVariable("place_id") String placeId) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Boolean scrap = pharmacyCommandService.scrapPharmacy(user, placeId);

        if (scrap) { // scrap이 true면 스크랩 동작 성공, scrap이 false면 스크랩 취소 동작 성공
            return ApiResponse.noResultSuccess(SuccessStatus.PHARMACY_SCRAP);
        } else {
            return ApiResponse.noResultSuccess(SuccessStatus.PHARMACY_UNSCRAP);
        }
    }

    @GetMapping("/{place_id}/scrap")
    @Operation(summary = "약국 스크랩 여부 조회 API", description = "약국 찾기 화면에서 조회한 약국이 이미 스크랩되었는지 확인합니다.")
    public ApiResponse<PharmacyResponseDTO.checkScrap> ifScrap(@Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader
                                ,@PathVariable("place_id") String placeId) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Boolean ifScrap = pharmacyQueryService.checkIfScrapPharmacy(placeId, user);
        return ApiResponse.of(SuccessStatus.PHARMACY_IF_SCRAP, new PharmacyResponseDTO.checkScrap(ifScrap));
    }

}

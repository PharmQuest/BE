package com.pharmquest.pharmquest.domain.pharmacy.web.controller;

import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyCommandService;
import com.pharmquest.pharmquest.domain.pharmacy.web.dto.PharmacyRequestDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
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
    public ApiResponse<Void> scrapPharmacy(@RequestBody @Valid PharmacyRequestDTO.ScrapDto request){
        pharmacyCommandService.scrapPharmacy(request.getUserId(), request.getPharmacyId());
        return ApiResponse.noResultSuccess(SuccessStatus.PHARMACY_SCRAP);
    }

}

package com.pharmquest.pharmquest.domain.mypage.web.controller;

import com.pharmquest.pharmquest.domain.mypage.converter.PharmacyScrapConverter;
import com.pharmquest.pharmquest.domain.mypage.service.ScrapService;
import com.pharmquest.pharmquest.domain.mypage.web.dto.ScrapResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import com.pharmquest.pharmquest.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class MyPageScrapController {

    private final ScrapService scrapService;
    private final JwtUtil jwtUtil;

    @GetMapping("/pharmacy")
    public ApiResponse<ScrapResponseDTO.PharmacyResponse> inquiryScrapedPharmacy(
            String authorizationHeader, @RequestParam("country") String country
    ) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        List<ScrapResponseDTO.PharmacyDto> pharmacies = scrapService.getPharmacies(user, country.trim());
        return ApiResponse.of(SuccessStatus.MY_PAGE_PHARMACY, PharmacyScrapConverter.dtoListToPharmacyResponse(pharmacies));
    }

}

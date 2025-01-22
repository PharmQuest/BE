package com.pharmquest.pharmquest.domain.mypage.converter;

import com.pharmquest.pharmquest.domain.mypage.web.dto.ScrapResponseDTO;

import java.util.List;

public class PharmacyScrapConverter {

    public static ScrapResponseDTO.PharmacyResponse dtoListToPharmacyResponse(List<ScrapResponseDTO.PharmacyDto> pharmacyDtoList) {
        return ScrapResponseDTO.PharmacyResponse.builder()
                .pharmacies(pharmacyDtoList)
                .count(pharmacyDtoList.size())
                .build();
    }

}

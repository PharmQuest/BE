package com.pharmquest.pharmquest.domain.mypage.converter;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;

import java.util.List;
import java.util.stream.Collectors;

public class MyPageConverter {

    public static List<MyPageResponseDTO.SupplementsResponseDto> toSupplementsDto(List<Supplements> supplements) {
        return supplements.stream()
                .map(supplement -> MyPageResponseDTO.SupplementsResponseDto.builder()
                        .id(supplement.getId())
                        .name(supplement.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public static MyPageResponseDTO.PharmacyResponse toPharmaciesDto(List<MyPageResponseDTO.PharmacyDto> pharmacyDtoList) {
        return MyPageResponseDTO.PharmacyResponse.builder()
                .pharmacies(pharmacyDtoList)
                .count(pharmacyDtoList.size())
                .build();
    }
}

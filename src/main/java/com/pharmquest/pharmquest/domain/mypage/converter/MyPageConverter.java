package com.pharmquest.pharmquest.domain.mypage.converter;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MyPageConverter {

    public static List<MyPageResponseDTO.SupplementsResponseDto> toSupplementsDto(List<Supplements> supplements) {
        return supplements.stream()
                .map(supplement -> MyPageResponseDTO.SupplementsResponseDto.builder()
                        .id(supplement.getId())
                        .name(supplement.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public static MyPageResponseDTO.PharmacyResponse toPharmaciesResponse(Page<MyPageResponseDTO.PharmacyDto> pharmacies) {

        List<MyPageResponseDTO.PharmacyDto> list = pharmacies.stream().toList();

        return MyPageResponseDTO.PharmacyResponse.builder()
                .pharmacies(list)
                .count((int) pharmacies.getTotalElements())
                .build();
    }
}

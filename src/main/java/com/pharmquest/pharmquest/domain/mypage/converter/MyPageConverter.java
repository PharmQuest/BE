package com.pharmquest.pharmquest.domain.mypage.converter;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MyPageConverter {
    private final SupplementsCategoryRepository supplementsCategoryRepository;

    public MyPageResponseDTO.SupplementsResponseDto toSupplementsDto(Supplements supplements) {
        List<String> categories = supplementsCategoryRepository.findCategoryNamesBySupplementId(supplements.getId());

        return MyPageResponseDTO.SupplementsResponseDto.builder()
                .id(supplements.getId())
                .name(supplements.getName())
                .image(supplements.getImage())
                .categories(categories)
                .build();
    }

    public static MyPageResponseDTO.PharmacyResponse toPharmaciesDto(List<MyPageResponseDTO.PharmacyDto> pharmacyDtoList) {
        return MyPageResponseDTO.PharmacyResponse.builder()
                .pharmacies(pharmacyDtoList)
                .count(pharmacyDtoList.size())
                .build();
    }
}

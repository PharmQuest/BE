package com.pharmquest.pharmquest.domain.mypage.converter;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MyPageConverter {
    private final SupplementsCategoryRepository supplementsCategoryRepository;

    public MyPageResponseDTO.SupplementsResponseDto toSupplementsDto(Supplements supplements, CategoryKeyword category) {
        List<String> categories = supplementsCategoryRepository.findCategoryNamesBySupplementId(supplements.getId());

        if (category == null || category == CategoryKeyword.전체) {
            return MyPageResponseDTO.SupplementsResponseDto.builder()
                    .id(supplements.getId())
                    .name(supplements.getName())
                    .image(supplements.getImage())
                    .categories(categories)
                    .build();
        } else {
            boolean isCategoryMatched = categories.stream()
                    .anyMatch(c -> c.equals(category.toString()));

            if (isCategoryMatched) {
                return MyPageResponseDTO.SupplementsResponseDto.builder()
                        .id(supplements.getId())
                        .name(supplements.getName())
                        .image(supplements.getImage())
                        .categories(categories)
                        .build();
            } else {
                return null;
            }
        }
    }

    public static MyPageResponseDTO.PharmacyResponse toPharmaciesDto(List<MyPageResponseDTO.PharmacyDto> pharmacyDtoList) {
        return MyPageResponseDTO.PharmacyResponse.builder()
                .pharmacies(pharmacyDtoList)
                .count(pharmacyDtoList.size())
                .build();
    }
}

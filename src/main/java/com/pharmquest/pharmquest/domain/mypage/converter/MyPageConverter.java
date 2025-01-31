package com.pharmquest.pharmquest.domain.mypage.converter;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public static MyPageResponseDTO.PharmacyResponse toPharmaciesResponse(Page<MyPageResponseDTO.PharmacyDto> pharmacies) {

        List<MyPageResponseDTO.PharmacyDto> list = pharmacies.stream().toList();

        return MyPageResponseDTO.PharmacyResponse.builder()
                .pharmacies(list)
                .count((int) pharmacies.getTotalElements())
                .build();
    }
}

package com.pharmquest.pharmquest.domain.supplements.converter;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupplementsConverter {
    private final SupplementsScrapRepository supplementsScrapRepository;

    public static SupplementsResponseDTO.SupplementsDto toSupplementsDto(Supplements supplements) {
        return SupplementsResponseDTO.SupplementsDto.builder()
                .name(supplements.getName())
                .image(supplements.getImage())
                .brand(supplements.getBrand())
                .build();
    }

    private boolean isSupplementScrappedByUser(Supplements supplement, Long userId) {
        if (userId == null) return false;
        return supplementsScrapRepository.existsByUserIdAndSupplementsId(userId, supplement.getId());
    }

    public SupplementsResponseDTO.SupplementsDto toDto(Supplements supplement, Long userId) {
        return SupplementsResponseDTO.SupplementsDto.builder()
                .name(supplement.getName())
                .image(supplement.getImage())
                .brand(supplement.getBrand())
                .isScrapped(isSupplementScrappedByUser(supplement, userId))
                .scrapCount(supplement.getScrapCount())
                .category4(supplement.getCategory4())
                .build();
    }

    public SupplementsResponseDTO.SupplementsSearchResponseDto toSearchDto(Supplements supplement, Long userId) {
        return SupplementsResponseDTO.SupplementsSearchResponseDto.builder()
                .name(supplement.getName())
                .image(supplement.getImage())
                .brand(supplement.getBrand())
                .isScrapped(isSupplementScrappedByUser(supplement, userId))
                .scrapCount(supplement.getScrapCount())
                .category4(supplement.getCategory4())
                .build();
    }

    public SupplementsResponseDTO.SupplementsDetailResponseDto toDetailDto(Supplements supplement, Long userId) {
        return SupplementsResponseDTO.SupplementsDetailResponseDto.builder()
                .name(supplement.getName())
                .image(supplement.getImage())
                .brand(supplement.getBrand())
                .maker(supplement.getMaker())
                .link(supplement.getLink())
                .isScrapped(isSupplementScrappedByUser(supplement, userId))
                .scrapCount(supplement.getScrapCount())
                .category1(supplement.getCategory1())
                .category2(supplement.getCategory2())
                .category3(supplement.getCategory3())
                .category4(supplement.getCategory4())
                .build();
    }
}
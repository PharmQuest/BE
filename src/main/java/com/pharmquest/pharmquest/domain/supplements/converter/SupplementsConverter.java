package com.pharmquest.pharmquest.domain.supplements.converter;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SupplementsConverter {
    private final SupplementsScrapRepository supplementsScrapRepository;
    private final SupplementsCategoryRepository supplementsCategoryRepository;
    private final SupplementsRepository supplementsRepository;

//    public static SupplementsResponseDTO.SupplementsDto toSupplementsDto(Supplements supplements) {
//        return SupplementsResponseDTO.SupplementsDto.builder()
//                .name(supplements.getName())
//                .image(supplements.getImage())
//                .brand(supplements.getBrand())
//                .build();
//    }

    private boolean isSupplementScrappedByUser(Supplements supplement, Long userId) {
        if (userId == null) return false;
        return supplementsScrapRepository.existsByUserIdAndSupplementsId(userId, supplement.getId());
    }

    public SupplementsResponseDTO.SupplementsDto toDto(Supplements supplement, Long userId) {
        List<String> categories = supplementsCategoryRepository.findCategoryNamesBySupplementId(supplement.getId());
        return SupplementsResponseDTO.SupplementsDto.builder()
                .id(supplement.getId())
                .name(supplement.getName())
                .image(supplement.getImage())
                .brand(supplement.getBrand())
                .isScrapped(isSupplementScrappedByUser(supplement, userId))
                .scrapCount(supplement.getScrapCount())
                .category4(supplement.getCategory4())
                .categories(categories)
                .build();
    }

    public SupplementsResponseDTO.SupplementsSearchResponseDto toSearchDto(Supplements supplement, Long userId) {
        List<String> categories = supplementsCategoryRepository.findCategoryNamesBySupplementId(supplement.getId());
        return SupplementsResponseDTO.SupplementsSearchResponseDto.builder()
                .id(supplement.getId())
                .name(supplement.getName())
                .image(supplement.getImage())
                .brand(supplement.getBrand())
                .isScrapped(isSupplementScrappedByUser(supplement, userId))
                .scrapCount(supplement.getScrapCount())
                .category4(supplement.getCategory4())
                .categories(categories)
                .build();
    }

    public SupplementsResponseDTO.SupplementsDetailResponseDto toDetailDto(Supplements supplement, Long userId) {
        List<String> categories = supplementsCategoryRepository.findCategoryNamesBySupplementId(supplement.getId());
        List<Supplements> relatedSupplements = supplementsRepository.findRelatedSupplements(supplement.getId(), PageRequest.of(0, 6));

        if (relatedSupplements.size() < 6){
            List<Long> excludeIds = new ArrayList<>();
            excludeIds.add(supplement.getId());
            excludeIds.addAll(relatedSupplements.stream()
                    .map(Supplements::getId).toList());

            List<Supplements> randomSupplements = supplementsRepository.findRandomSupplementsByCountry(supplement.getId(), excludeIds, PageRequest.of(0, 6 - relatedSupplements.size()));
            relatedSupplements.addAll(randomSupplements);
        }

        return SupplementsResponseDTO.SupplementsDetailResponseDto.builder()
                .id(supplement.getId())
                .name(supplement.getName())
                .image(supplement.getImage())
                .brand(supplement.getBrand())
                .maker(supplement.getMaker())
                .isScrapped(isSupplementScrappedByUser(supplement, userId))
                .scrapCount(supplement.getScrapCount())
                .isScrapped(isSupplementScrappedByUser(supplement, userId))
                .category1(supplement.getCategory1())
                .category2(supplement.getCategory2())
                .category3(supplement.getCategory3())
                .category4(supplement.getCategory4())
                .dosage(supplement.getDosage())
                .purpose(supplement.getPurpose())
                .warning(supplement.getWarning())
                .categories(categories)
                .relatedSupplements(relatedSupplements.stream()
                        .map(s -> SupplementsResponseDTO.RelatedSupplementDto.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .image(s.getImage())
                                .brand(s.getBrand())
                                .maker(s.getMaker())
                                .isScrapped(isSupplementScrappedByUser(s, userId))
                                .scrapCount(s.getScrapCount())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
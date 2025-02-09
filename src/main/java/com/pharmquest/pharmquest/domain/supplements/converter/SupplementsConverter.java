package com.pharmquest.pharmquest.domain.supplements.converter;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private String processCountryName(Country country) {
        if (country == Country.KOREA) {
            return "한국";
        }
        else if (country == Country.USA) {
            return "미국";
        }
        else if (country == Country.JAPAN) {
            return "일본";
        }
        return "";
    }

    private String processProductName(String name) {
        return name.replaceAll("^\\[(한국|미국|일본)\\]\\s*", "");
    }

    public SupplementsResponseDTO.SupplementsDto toDto(Supplements supplement, Long userId) {
        List<String> categories = supplementsCategoryRepository.findCategoryNamesBySupplementId(supplement.getId());
        return SupplementsResponseDTO.SupplementsDto.builder()
                .id(supplement.getId())
                .name(supplement.getName())
                .image(supplement.getImage())
                .brand(supplement.getBrand())
                .productName(processProductName(supplement.getName()))
                .country(processCountryName(supplement.getCountry()))
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
                .productName(processProductName(supplement.getName()))
                .country(processCountryName(supplement.getCountry()))
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

        List<String> dosageList = supplement.getDosage() != null ?
                Arrays.stream(supplement.getDosage().split("(?<=\\.)\\s+"))
                        .filter(s -> !s.trim().isEmpty())
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        List<String> purposeList = supplement.getPurpose() != null ?
                Arrays.stream(supplement.getPurpose().split("(?<=\\.)\\s+"))
                        .filter(s -> !s.trim().isEmpty())
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        List<String> warningList = supplement.getWarning() != null ?
                Arrays.stream(supplement.getWarning().split("(?<=\\.)\\s+"))
                        .filter(s -> !s.trim().isEmpty())
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        List<String> productCategory = Stream.of(
                        supplement.getCategory1(),
                        supplement.getCategory2(),
                        supplement.getCategory3(),
                        supplement.getCategory4())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return SupplementsResponseDTO.SupplementsDetailResponseDto.builder()
                .id(supplement.getId())
                .name(supplement.getName())
                .image(supplement.getImage())
                .brand(supplement.getBrand())
                .maker(supplement.getMaker())
                .productName(processProductName(supplement.getName()))
                .country(processCountryName(supplement.getCountry()))
                .isScrapped(isSupplementScrappedByUser(supplement, userId))
                .scrapCount(supplement.getScrapCount())
                .productCategory(productCategory)
                .dosage(dosageList)
                .purpose(purposeList)
                .warning(warningList)
                .categories(categories)
                .relatedSupplements(relatedSupplements.stream()
                        .map(s -> {
                            List<String> relatedCategories = supplementsCategoryRepository.findCategoryNamesBySupplementId(s.getId());
                            return SupplementsResponseDTO.RelatedSupplementDto.builder()
                                    .id(s.getId())
                                    .name(s.getName())
                                    .image(s.getImage())
                                    .brand(s.getBrand())
                                    .maker(s.getMaker())
                                    .productName(processProductName(supplement.getName()))
                                    .country(processCountryName(supplement.getCountry()))
                                    .categories(relatedCategories)
                                    .isScrapped(isSupplementScrappedByUser(s, userId))
                                    .scrapCount(s.getScrapCount())
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
    }
}
package com.pharmquest.pharmquest.domain.supplements.service.Supplements;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.converter.SupplementsConverter;
import com.pharmquest.pharmquest.domain.supplements.data.Category;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsCategory;
import com.pharmquest.pharmquest.domain.supplements.repository.CategoryRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import com.pharmquest.pharmquest.domain.supplements.service.DailyMed.DailyMedService;
import com.pharmquest.pharmquest.domain.supplements.service.Naver.NaverShoppingService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.DailyMedResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplementsServiceImpl implements SupplementsService {

    private static final List<String> CATEGORY_KEYWORDS = Arrays.asList(
            "햇볕", "자외선", "치주", "가려움증", "눈건강", "면역력", "관절", "피부건강",
            "간건강", "미네랄", "뼈건강", "고혈압", "수면", "피부", "두피", "화상", "허리",
            "상처"
    );

    private final SupplementsCategoryRepository supplementsCategoryRepository;
    private final DailyMedService dailyMedService;
    private final NaverShoppingService naverShoppingService;
    private final SupplementsRepository supplementsRepository;
    private final SupplementsConverter supplementsConverter;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<SupplementsResponseDTO.SupplementsDto> getSupplements(String category, Pageable pageable, Long userId) {
        Page<Supplements> supplementsPage;
        if (category != null && !category.isEmpty()) {
            List<Long> supplementIds = supplementsCategoryRepository.findSupplementIdByCategoryName(category);
            if (supplementIds.isEmpty()) {
                return new PageImpl<>(new ArrayList<>(), pageable, 0);
            }
            supplementsPage = supplementsRepository.findByIdIn(supplementIds, pageable);
        } else {
            supplementsPage = supplementsRepository.findAll(pageable);
        }

        List<SupplementsResponseDTO.SupplementsDto> dtoList = supplementsPage.getContent().stream()
                .map(supplement -> supplementsConverter.toDto(supplement, userId))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, supplementsPage.getTotalElements());
    }

    @Override
    public Page<SupplementsResponseDTO.SupplementsSearchResponseDto> searchSupplements(String keyword, Country country, Pageable pageable, Long userId) {
        Page<Supplements> supplementsPage;
        if (keyword != null && country != null) {
            supplementsPage = supplementsRepository.findByNameContainingAndCountry(keyword, country, pageable);
        } else if (keyword != null) {
            supplementsPage = supplementsRepository.findByNameContaining(keyword, pageable);
        } else {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        List<SupplementsResponseDTO.SupplementsSearchResponseDto> dtoList = supplementsPage.getContent().stream()
                .map(supplement -> supplementsConverter.toSearchDto(supplement, userId))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, supplementsPage.getTotalElements());
    }

    @Override
    public SupplementsResponseDTO.SupplementsDetailResponseDto getSupplementById(Long id, Long userId) {
        Supplements supplement = supplementsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplement not found"));
        return supplementsConverter.toDetailDto(supplement, userId);
    }

    @Override
    @Transactional
    public boolean saveSupplements() {
        try {
            List<String> supplementsNames = Arrays.asList(
                    "일본 인기 영양제",
                    "중국 인기 영양제"
            );

            supplementsNames.forEach(searchKeyword ->
                    naverShoppingService.loadProducts(searchKeyword).stream()
                            .map(dto -> {
                                try {
                                    Country country = getCountryFromSearchKeyword(searchKeyword);
                                    String cleanedName = cleanProductName(dto.getName(), country);

                                    if (supplementsRepository.existsByName(cleanedName)) {
                                        return null;
                                    }
                                    return Supplements.builder()
                                            .name(cleanedName)
                                            .image(dto.getImage())
                                            .brand(dto.getBrand())
                                            .maker(dto.getMaker())
                                            .category1(dto.getCategory1())
                                            .category2(dto.getCategory2())
                                            .category3(dto.getCategory3())
                                            .category4(dto.getCategory4())
                                            .dosage("")
                                            .purpose("")
                                            .warning("")
                                            .country(country)
                                            .scrapCount(0)
                                            .build();
                                } catch (Exception e) {
                                    return null;
                                }
                            })
                            .forEach(supplement -> {
                                if (supplement != null) {
                                    supplementsRepository.save(supplement);
                                }
                            })
            );

            List<DailyMedResponseDTO.ExtractedInfo> extractedInfoList = dailyMedService.extractSupplementInfo();
            extractedInfoList.stream()
                    .map(info -> {
                        try {
                            DailyMedResponseDTO.DetailInfo detailInfo = dailyMedService.getDetailInfo(info.getSetid(), info.getTitle());
                            String cleanedName = cleanProductName(detailInfo.getTitle(), Country.USA);

                            if (supplementsRepository.existsByName(cleanedName)) {
                                return null;
                            }

                            Supplements supplement = Supplements.builder()
                                    .name(cleanedName)
                                    .image(detailInfo.getImageUrl())
                                    .brand(detailInfo.getManufacturer())
                                    .maker(detailInfo.getManufacturer())
                                    .category1("식품")
                                    .category2("건강식품")
                                    .category3("영양제")
                                    .category4("기타건강보조식품")
                                    .dosage(detailInfo.getDosage())
                                    .purpose(detailInfo.getPurpose())
                                    .warning(detailInfo.getWarning())
                                    .country(Country.USA)
                                    .scrapCount(0)
                                    .build();

                            String fullText = detailInfo.getDosage() + " " + detailInfo.getPurpose();
                            supplement = supplementsRepository.save(supplement);
                            processCateogories(fullText, supplement);

                            return supplement;
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(supplement -> supplement != null)
                    .collect(Collectors.toList());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void processCateogories(String text, Supplements supplement) {
        CATEGORY_KEYWORDS.stream()
                .filter(keyword -> text != null && text.contains(keyword))
                .forEach(keyword -> {
                    Category category = categoryRepository.findCategoryByName(keyword)
                            .orElseGet(() -> categoryRepository.save(Category.builder()
                                    .name(keyword)
                                    .build()));
                    SupplementsCategory supplementsCategory = new SupplementsCategory();
                    supplementsCategory.setSupplement(supplement);
                    supplementsCategory.setCategory(category);
                    supplementsCategoryRepository.save(supplementsCategory);
                });
    }


    private String cleanProductName(String name, Country country) {
        String cleaned = name.replaceAll("\\[?(미국|일본|중국|한국|)\\]?", "")
                .replaceAll("\\((미국|일본|중국|한국)\\)", "");

        cleaned = cleaned.replaceAll("\\d+개|\\d+개입|\\d+정|\\d+캡슐|\\d+일분|\\d+박스|\\d+매|\\d+통|\\d+등|\\d+Kg|\\d+kg|\\d+G|\\d+g|\\d+Mg|\\d+mg", "");

        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        String prefix = switch (country) {
            case USA -> "[미국]";
            case JAPAN -> "[일본]";
            case CHINA -> "[중국]";
            default -> "";
        };

        return prefix + " " + cleaned;
    }

    private Country getCountryFromSearchKeyword(String keyword) {
        if (keyword.contains("미국")) {
            return Country.USA;
        } else if (keyword.contains("일본")) {
            return Country.JAPAN;
        } else if (keyword.contains("중국")) {
            return Country.CHINA;
        } else {
            return Country.NONE;
        }
    }
}
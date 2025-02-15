package com.pharmquest.pharmquest.domain.supplements.service.Supplements;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.converter.SupplementsConverter;
import com.pharmquest.pharmquest.domain.supplements.data.Category;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryGroup;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsCategory;
import com.pharmquest.pharmquest.domain.supplements.repository.CategoryRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import com.pharmquest.pharmquest.domain.supplements.service.Advertisement.AdvertisementService;
import com.pharmquest.pharmquest.domain.supplements.service.DailyMed.DailyMedService;
import com.pharmquest.pharmquest.domain.supplements.service.Naver.NaverShoppingService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.AdResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.web.dto.DailyMedResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplementsServiceImpl implements SupplementsService {

    private final SupplementsCategoryRepository supplementsCategoryRepository;
    private final DailyMedService dailyMedService;
    private final SupplementsRepository supplementsRepository;
    private final SupplementsConverter supplementsConverter;
    private final CategoryRepository categoryRepository;
    private final AdvertisementService advertisementService;

    //영양제 리스트 조회
    @Override
    public SupplementsResponseDTO.SupplementsPageResponseDto getSupplements(CategoryGroup categoryGroup, Pageable pageable, Long userId) {
        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "scrapCount")
        );

        Page<Supplements> supplementsPage;
        if (categoryGroup == null || categoryGroup == CategoryGroup.전체) {
            supplementsPage = supplementsRepository.findAll(pageableWithSort);
        }
        else {
            List<Long> supplementIds = supplementsCategoryRepository.findSupplementIdByCategoryName(categoryGroup.getCategories());
            if (supplementIds.isEmpty()) {
                throw new CommonExceptionHandler(ErrorStatus.SUPPLEMENTS_NO_FILTERED);
            }
            supplementsPage = supplementsRepository.findByIdIn(supplementIds, pageableWithSort);
        }

        if (supplementsPage.isEmpty()) {
            throw new CommonExceptionHandler(ErrorStatus.SUPPLEMENTS_NO_FILTERED);
        }

        List<Long> pageSupplementIds = supplementsPage.getContent().stream()
                .map(Supplements::getId)
                .collect(Collectors.toList());

        Map<Long, List<String>> categoryMap = supplementsCategoryRepository
                .findCategoryNamesBySupplementIds(pageSupplementIds).stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(
                                row -> (String) row[1],
                                Collectors.toList()
                        )
                ));

        List<SupplementsResponseDTO.SupplementsDto> supplementsDtos = supplementsPage.getContent().stream()
                .map(supplement -> {
                    SupplementsResponseDTO.SupplementsDto dto = supplementsConverter.toDto(supplement, userId, categoryMap);
                    dto.setType("SUPPLEMENT");
                    return dto;
                })
                .collect(Collectors.toList());

        // 광고 가져오기
        AdResponseDTO.AdResponseDto ad = advertisementService.getAdvertisementByPage(supplementsPage.getNumber() + 1);

        List<SupplementsResponseDTO.SupplementsDto> items = new ArrayList<>();
        for (int i = 0; i < supplementsDtos.size(); i++) {
            // 영양제 추가
            SupplementsResponseDTO.SupplementsDto supplement = supplementsDtos.get(i);
            supplement.setType("SUPPLEMENT");
            items.add(supplement);

            // 9개의 영양제 후에 광고 삽입
            if ((i + 1) % 9 == 0 && ad != null) {
                SupplementsResponseDTO.SupplementsDto adDto = SupplementsResponseDTO.SupplementsDto.builder()
                        .type("AD")
                        .id(-ad.getId())
                        .name(ad.getName())
                        .country("광고")
                        .productName(supplementsConverter.processAdName(ad.getName()))
                        .image(ad.getImage())
                        .brand("")
                        .scrapCount(0)
                        .category4("")
                        .categories(new ArrayList<>())
                        .selectCategories(new ArrayList<>())
                        .isAd(true)
                        .isScrapped(false)
                        .build();

                items.add(adDto);
            }
        }

        return SupplementsResponseDTO.SupplementsPageResponseDto.builder()
                .amountPage(supplementsPage.getTotalPages())
                .amountCount((int) supplementsPage.getTotalElements())
                .currentPage(supplementsPage.getNumber() + 1)
                .currentCount(supplementsPage.getNumberOfElements())
                .items(items)
                .build();
    }

    //영양제 검색
    @Override
    public SupplementsResponseDTO.SupplementsSearchPageResponseDto searchSupplements(String keyword, Country country, Pageable pageable, Long userId) {
        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "scrapCount")
        );
        Page<Supplements> supplementsPage;
        if (keyword == null) {
            throw new CommonExceptionHandler(ErrorStatus.SUPPLEMENTS_NO_KEYWORD);
        }

        if (country != null) {
            supplementsPage = supplementsRepository.findByNameContainingAndCountry(keyword, country, pageableWithSort);
        } else {
            supplementsPage = supplementsRepository.findByNameContaining(keyword, pageableWithSort);
        }

        if (supplementsPage.isEmpty()) {
            throw new CommonExceptionHandler(ErrorStatus.SUPPLEMENTS_NO_SEARCH_RESULT);
        }

        List<Long> pageSupplementIds = supplementsPage.getContent().stream()
                .map(Supplements::getId)
                .collect(Collectors.toList());

        Map<Long, List<String>> categoryMap = supplementsCategoryRepository
                .findCategoryNamesBySupplementIds(pageSupplementIds).stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(
                                row -> (String) row[1],
                                Collectors.toList()
                        )
                ));

        List<SupplementsResponseDTO.SupplementsSearchResponseDto> supplementsDtos = supplementsPage.getContent().stream()
                .map(supplement -> {
                    SupplementsResponseDTO.SupplementsSearchResponseDto dto = supplementsConverter.toSearchDto(supplement, userId, categoryMap);
                    dto.setType("SUPPLEMENT");
                    return dto;
                })
                .collect(Collectors.toList());

        // 광고 가져오기
        AdResponseDTO.AdResponseDto ad = advertisementService.getAdvertisementByPage(supplementsPage.getNumber() + 1);

        List<SupplementsResponseDTO.SupplementsSearchResponseDto> items = new ArrayList<>();
        for (int i = 0; i < supplementsDtos.size(); i++) {
            // 영양제 추가
            SupplementsResponseDTO.SupplementsSearchResponseDto supplement = supplementsDtos.get(i);
            supplement.setType("SUPPLEMENT");
            items.add(supplement);

            // 9개의 영양제 후에 광고 삽입
            if ((i + 1) % 9 == 0 && ad != null) {
                SupplementsResponseDTO.SupplementsSearchResponseDto adDto = SupplementsResponseDTO.SupplementsSearchResponseDto.builder()
                        .type("AD")
                        .id(-ad.getId())
                        .name(ad.getName())
                        .country("광고")
                        .productName(supplementsConverter.processAdName(ad.getName()))
                        .image(ad.getImage())
                        .brand("")
                        .scrapCount(0)
                        .category4("")
                        .categories(new ArrayList<>())
                        .selectCategories(new ArrayList<>())
                        .isAd(true)
                        .isScrapped(false)
                        .build();

                items.add(adDto);
            }
        }

        return SupplementsResponseDTO.SupplementsSearchPageResponseDto.builder()
                .amountPage(supplementsPage.getTotalPages())
                .amountCount((int) supplementsPage.getTotalElements())
                .currentPage(supplementsPage.getNumber() + 1)
                .currentCount(supplementsPage.getNumberOfElements())
                .items(items)
                .build();
    }

    //영양제 상세조회
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
            saveDailyMedSupplements();
//            saveNaverSupplements();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void saveDailyMedSupplements() {
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
    }

//    private void saveNaverSupplements() {
//        List<String> supplementsNames = Arrays.asList(
//                "한국 인기 영양제"
//        );
//
//        supplementsNames.forEach(searchKeyword ->
//                naverShoppingService.loadProducts(searchKeyword).stream()
//                        .map(dto -> {
//                            try {
//                                Country country = getCountryFromSearchKeyword(searchKeyword);
//                                String cleanedName = cleanProductName(dto.getName(), country);
//
//                                if (supplementsRepository.existsByName(cleanedName)) {
//                                    return null;
//                                }
//                                return Supplements.builder()
//                                        .name(cleanedName)
//                                        .image(dto.getImage())
//                                        .brand(dto.getBrand())
//                                        .maker(dto.getMaker())
//                                        .category1(dto.getCategory1())
//                                        .category2(dto.getCategory2())
//                                        .category3(dto.getCategory3())
//                                        .category4(dto.getCategory4())
//                                        .dosage("")
//                                        .purpose("")
//                                        .warning("")
//                                        .country(country)
//                                        .scrapCount(0)
//                                        .build();
//                            } catch (Exception e) {
//                                return null;
//                            }
//                        })
//                        .filter(supplement -> supplement != null)
//                        .forEach(supplementsRepository::save)
//        );
//    }

    private void processCateogories(String text, Supplements supplement) {
        Arrays.stream(CategoryKeyword.values())
                .map(Enum::name)
                .filter(keyword -> text != null && text.contains(keyword))
                .forEach(keyword -> {
                    Category category = categoryRepository.findCategoryByName(keyword)
                            .orElseGet(() -> categoryRepository.save(Category.builder()
                                    .name(keyword)
                                    .build()));

                    SupplementsCategory supplementsCategory = SupplementsCategory.builder()
                            .supplement(supplement)
                            .category(category)
                            .build();
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
}
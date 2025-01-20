package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.converter.SupplementsConverter;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplementsServiceImpl implements SupplementsService {

    private final NaverShoppingService naverShoppingService;
    private final SupplementsRepository supplementsRepository;
    private final SupplementsConverter supplementsConverter;

    @Override
    public Page<SupplementsResponseDTO.SupplementsDto> getSupplements(String category, Pageable pageable, Long userId) {
        Page<Supplements> supplementsPage;
        if (category != null) {
            supplementsPage = supplementsRepository.findByCategory4(category, pageable);
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

//    @Scheduled(cron = "0 0 0 */2 * *")
//    @Transactional
//    public void scheduledSaveSupplements() {
//        saveSupplements();
//    }

    @Override
    @Transactional
    public boolean saveSupplements() {
        try {
            List<String> supplementsNames = Arrays.asList(
                    "미국 인기 영양제",
                    "일본 인기 영양제",
                    "중국 인기 영양제"
            );

            supplementsNames.forEach(searchKeyword ->
                    naverShoppingService.loadProducts(searchKeyword).stream()
                            .map(dto -> {
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
                                        .link(dto.getLink())
                                        .category1(dto.getCategory1())
                                        .category2(dto.getCategory2())
                                        .category3(dto.getCategory3())
                                        .category4(dto.getCategory4())
                                        .description("")
                                        .country(country)
                                        .scrapCount(0)
                                        .build();
                            })
                            .forEach(supplement -> {
                                if (supplement != null) {
                                    supplementsRepository.save(supplement);
                                }
                            })
            );
            return true;
        } catch (Exception e) {
            return false;
        }
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
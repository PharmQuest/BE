package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.domain.Enums.Nation;
import com.pharmquest.pharmquest.domain.supplements.domain.Supplements;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplementsServiceImpl implements SupplementsService {

    private final NaverShoppingService naverShoppingService;
    private final SupplementsRepository supplementsRepository;

    @Override
    public List<Supplements> getSupplements() {
        List<String> supplementsNames = Arrays.asList(
                "미국 인기 영양제",
                "일본 인기 영양제",
                "중국 인기 영양제"
        );
        return supplementsNames.stream()
                .flatMap(name -> naverShoppingService.loadProducts(name).stream())
                .map(dto -> Supplements.builder()
                        .name(dto.getName())
                        .image(dto.getImage())
                        .brand(dto.getBrand())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveSupplements(){
        List<String> supplementsNames = Arrays.asList(
                "미국 인기 영양제",
                "일본 인기 영양제",
                "중국 인기 영양제"
        );
        supplementsNames.forEach(searchKeyword ->
                naverShoppingService.loadProducts(searchKeyword).stream()
                        .map(dto -> Supplements.builder()
                            .name(dto.getName())
                            .image(dto.getImage())
                            .brand(dto.getBrand())
                            .isScrapped(false)
                            .category1(dto.getCategory1())
                            .category2(dto.getCategory2())
                            .category3(dto.getCategory3())
                            .category4(dto.getCategory4())
                            .nation(getNationFromSearchKeyword(searchKeyword))
                            .build()
                )
                .forEach(supplements -> supplementsRepository.save(supplements))
        );
    }

    private Nation getNationFromSearchKeyword(String keyword) {
        if (keyword.contains("미국")){
            return Nation.USA;
        } else if (keyword.contains("일본")) {
            return Nation.JAPAN;
        } else if (keyword.contains("중국")) {
            return Nation.CHINA;
        } else{
            return Nation.KOREA;
        }
    }
}
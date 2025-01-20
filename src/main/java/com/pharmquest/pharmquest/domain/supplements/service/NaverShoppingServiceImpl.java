package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import com.pharmquest.pharmquest.global.config.NaverShoppingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NaverShoppingServiceImpl implements NaverShoppingService {
    private final RestTemplate restTemplate;
    private final NaverShoppingConfig naverShoppingConfig;

    @Value("${naver.api.url}")
    private String naverApiUrl;

    @Override
    public List<SupplementsResponseDTO.SupplementsInternalDto> loadProducts(String query) {
        HttpEntity<String> httpEntity = new HttpEntity<>(naverShoppingConfig.naverApiHttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(naverApiUrl)
                .queryParam("query", query)
                .queryParam("display", 50);

        ResponseEntity<Map> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                Map.class
        );

        List<Map> items = (List<Map>) response.getBody().get("items");
        return items.stream()
                .filter(item -> {
                    String category2 = item.get("category2") != null ? item.get("category2").toString() : "";
                    return "건강식품".equals(category2);
                })
                .map(item -> {
                    String brand = item.get("brand") != null ? item.get("brand").toString() : "";
                    String maker = item.get("maker") != null ? item.get("maker").toString() : "";
                    String category1 = item.get("category1") != null ? item.get("category1").toString() : "";
                    String category2 = item.get("category2") != null ? item.get("category2").toString() : "";
                    String category3 = item.get("category3") != null ? item.get("category3").toString() : "";
                    String category4 = item.get("category4") != null ? item.get("category4").toString() : "";

                    return SupplementsResponseDTO.SupplementsInternalDto.builder()
                            .name(item.get("title").toString().replaceAll("<[^>]*>", ""))
                            .image(item.get("image").toString())
                            .link(item.get("link").toString())
                            .brand(brand)
                            .maker(maker)
                            .category1(category1)
                            .category2(category2)
                            .category3(category3)
                            .category4(category4)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
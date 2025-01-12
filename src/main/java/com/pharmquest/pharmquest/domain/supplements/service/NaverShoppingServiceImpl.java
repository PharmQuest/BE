package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.config.NaverShoppingConfig;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
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
    public List<SupplementsResponseDTO.SupplementsDto> loadProducts(String query) {
        HttpEntity<String> httpEntity = new HttpEntity<>(naverShoppingConfig.naverApiHttpHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(naverApiUrl)
                .queryParam("query", query)
                .queryParam("display", 10);

        ResponseEntity<Map> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                Map.class
        );

        List<Map> items = (List<Map>) response.getBody().get("items");
        return items.stream()
                .map(item -> SupplementsResponseDTO.SupplementsDto.builder()
                        .name(item.get("title").toString())
                        .image(item.get("image").toString())
                        .brand(item.get("brand").toString())
                        .build())
                .collect(Collectors.toList());
    }
}
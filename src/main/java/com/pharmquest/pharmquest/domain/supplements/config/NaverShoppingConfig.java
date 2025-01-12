package com.pharmquest.pharmquest.domain.supplements.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class NaverShoppingConfig {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpHeaders naverApiHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        return headers;
    }
}
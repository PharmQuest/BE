package com.pharmquest.pharmquest.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.fda.gov") // 기본 URL 설정
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024) // 10MB로 증가
                        ).build())
                .build();
    }
}
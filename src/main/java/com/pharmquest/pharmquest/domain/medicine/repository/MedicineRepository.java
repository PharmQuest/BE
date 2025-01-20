package com.pharmquest.pharmquest.domain.medicine.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class MedicineRepository {

    private final WebClient webClient;

    @Value("${fda.api.api-key}")
    private String apiKey;

    public MedicineRepository(WebClient webClient) {
        this.webClient = webClient;
    }

    //FDA API를 호출하여 약물 데이터를 가져옵니다.
    public String fetchMedicineData(String query, int limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/drug/label.json")
                        .queryParam("search", query)
                        .queryParam("limit", limit)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    //DailyMed API를 호출하여 주어진 SPL Set ID에 대한 이미지 데이터를 가져옵니다.
    public String fetchImageData(String splSetId) {
        return webClient.get()
                .uri("https://dailymed.nlm.nih.gov/dailymed/services/v2/spls/" + splSetId + "/media.json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

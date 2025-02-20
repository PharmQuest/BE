package com.pharmquest.pharmquest.domain.medicine.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class KoreanMedicineRepository {

    @Value("${openapi.medicine.base-url}")
    private String baseUrl;

    @Value("${openapi.medicine.api-key}")
    private String serviceKey;

    private final WebClient webClient;

    /**
     * ✅ 1~5페이지의 약품 데이터를 비동기로 가져오기
     */
    public Mono<List<String>> fetchMedicineData() {
        List<Mono<String>> apiRequests = new ArrayList<>();

        for (int i = 1; i <= 1; i++) {
            try {
                String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
                String encodedPageNo = URLEncoder.encode(String.valueOf(i), StandardCharsets.UTF_8.toString());
                String encodedNumOfRows = URLEncoder.encode("100", StandardCharsets.UTF_8.toString());
                String encodedType = URLEncoder.encode("json", StandardCharsets.UTF_8.toString());

                URI uri = URI.create(String.format("%s/getDrbEasyDrugList?serviceKey=%s&pageNo=%s&numOfRows=%s&type=%s",
                        baseUrl, encodedServiceKey, encodedPageNo, encodedNumOfRows, encodedType));

                log.info("🔹 API 요청 URI (페이지 {}): {}", i, uri);

                apiRequests.add(webClient.get()
                        .uri(uri)
                        .header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(String.class));

            } catch (UnsupportedEncodingException e) {
                log.error("❌ URL 인코딩 오류 발생: {}", e.getMessage());
            }
        }

        return Mono.zip(apiRequests, results -> {
            List<String> responseList = new ArrayList<>();
            for (Object result : results) {
                responseList.add((String) result); // 명시적 캐스팅
            }
            return responseList;
        });
    }


    public Mono<List<String>> fetchMedicineDataWithKeyword(String keyword) {
        List<Mono<String>> apiRequests = new ArrayList<>();

        for (int i = 1; i <= 25; i++) {
            try {
                String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
                String encodedPageNo = URLEncoder.encode(String.valueOf(i), StandardCharsets.UTF_8.toString());
                String encodedNumOfRows = URLEncoder.encode("100", StandardCharsets.UTF_8.toString());
                String encodedType = URLEncoder.encode("json", StandardCharsets.UTF_8.toString());

                URI uri = URI.create(String.format("%s/getDrbEasyDrugList?serviceKey=%s&pageNo=%s&numOfRows=%s&type=%s",
                        baseUrl, encodedServiceKey, encodedPageNo, encodedNumOfRows, encodedType));

                log.info("🔹 API 요청 URI (페이지 {}): {}", i, uri);

                apiRequests.add(webClient.get()
                        .uri(uri)
                        .header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(String.class));

            } catch (UnsupportedEncodingException e) {
                log.error("❌ URL 인코딩 오류 발생: {}", e.getMessage());
            }
        }

        return Mono.zip(apiRequests, results -> {
            List<String> responseList = new ArrayList<>();
            for (Object result : results) {
                responseList.add((String) result);
            }
            return responseList;
        });
    }
}

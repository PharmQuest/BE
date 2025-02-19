package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.web.dto.KoreanMedicineResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Slf4j
@Tag(name = "의약품 API", description = "식품의약품안전처 OpenAPI 연동")
@RestController
@RequestMapping("/api/medicine")
public class MedicineTestController {

    @Value("${openapi.medicine.base-url}")
    private String baseUrl;

    @Value("${openapi.medicine.api-key}") // ✅ 디코딩된 API Key 사용!
    private String serviceKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MedicineTestController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build(); // ✅ baseUrl 제거 (절대 URL 사용 시)
    }
    @GetMapping("/search")
    public Mono<ResponseEntity<String>> searchMedicine(
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "10") String numOfRows,
            @RequestParam(defaultValue = "json") String type) throws UnsupportedEncodingException {

        log.info("🔹 원본 API Key (yml에서 로드됨): {}", serviceKey);

        // ✅ API Key 및 파라미터를 URL 인코딩 (디코딩된 원본 키 사용!)
        String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
        String encodedPageNo = URLEncoder.encode(pageNo, StandardCharsets.UTF_8.toString());
        String encodedNumOfRows = URLEncoder.encode(numOfRows, StandardCharsets.UTF_8.toString());
        String encodedType = URLEncoder.encode(type, StandardCharsets.UTF_8.toString());

        // ✅ URI 객체로 변환하여 요청
        URI uri = URI.create(String.format("%s/getDrbEasyDrugList?serviceKey=%s&pageNo=%s&numOfRows=%s&type=%s",
                baseUrl, encodedServiceKey, encodedPageNo, encodedNumOfRows, encodedType));

        log.info("🔹 최종 요청 URI: {}", uri);

        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0") // ✅ User-Agent 추가
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) // ✅ JSON 응답 강제 요청
                .retrieve()
                .toEntity(String.class)
                .doOnSuccess(response -> {
                    log.info("✅ API 응답 상태 코드: {}", response.getStatusCode());
                    log.info("✅ API 응답 본문: {}", response.getBody());
                })
                .doOnError(error -> log.error("❌ API 요청 중 오류 발생: {}", error.getMessage()));
    }


    @GetMapping("/search/by-effect")
    public Mono<ResponseEntity<List<KoreanMedicineResponseDTO>>> searchMedicineByEffect(
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "100") String numOfRows,
            @RequestParam(required = false) String efcyQesitm,
            @RequestParam(defaultValue = "json") String type) throws UnsupportedEncodingException {

        log.info("🔹 원본 API Key (yml에서 로드됨): {}", serviceKey);

        // ✅ 1~5페이지까지 데이터를 가져오기 위한 요청 리스트
        List<Mono<String>> apiRequests = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
            String encodedPageNo = URLEncoder.encode(String.valueOf(i), StandardCharsets.UTF_8.toString());
            String encodedNumOfRows = URLEncoder.encode(numOfRows, StandardCharsets.UTF_8.toString());
            String encodedType = URLEncoder.encode(type, StandardCharsets.UTF_8.toString());

            // ✅ URI 객체로 변환하여 요청
            URI uri = URI.create(String.format("%s/getDrbEasyDrugList?serviceKey=%s&pageNo=%s&numOfRows=%s&type=%s",
                    baseUrl, encodedServiceKey, encodedPageNo, encodedNumOfRows, encodedType));

            log.info("🔹 요청 URI (페이지 {}): {}", i, uri);

            // ✅ WebClient 비동기 요청 추가
            apiRequests.add(webClient.get()
                    .uri(uri)
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class));
        }

        // ✅ 모든 요청을 병렬로 실행한 후 결과 합치기
        return Mono.zip(apiRequests, results -> {
            List<String> allResponses = Arrays.stream(results).map(obj -> (String) obj).toList();
            List<KoreanMedicineResponseDTO> medicineList = filterAndConvertToDTO(allResponses, efcyQesitm, 10);
            return ResponseEntity.ok(medicineList);
        }).doOnError(error -> log.error("❌ API 요청 중 오류 발생: {}", error.getMessage()));
    }

    /**
     * 🔎 API 응답 JSON에서 특정 증상을 포함하는 데이터만 필터링 후 DTO 변환 (최대 개수 설정 가능)
     */
    private List<KoreanMedicineResponseDTO> filterAndConvertToDTO(List<String> responses, String effect, int maxResults) {
        try {
            List<KoreanMedicineResponseDTO> filteredItems = new ArrayList<>();

            for (String responseBody : responses) {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode itemsNode = rootNode.path("body").path("items");

                if (!itemsNode.isArray()) continue;

                Iterator<JsonNode> elements = itemsNode.elements();
                while (elements.hasNext()) {
                    JsonNode item = elements.next();
                    String efcyText = item.path("efcyQesitm").asText("");

                    if (efcyText.contains(effect)) {
                        KoreanMedicineResponseDTO dto = KoreanMedicineResponseDTO.builder()
                                .entpName(item.path("entpName").asText(""))
                                .itemName(item.path("itemName").asText(""))
                                .itemSeq(item.path("itemSeq").asText(""))
                                .efcyQesitm(efcyText)
                                .useMethodQesitm(item.path("useMethodQesitm").asText(""))
                                .atpnQesitm(item.path("atpnQesitm").asText(""))
                                .intrcQesitm(item.path("intrcQesitm").asText(""))
                                .seQesitm(item.path("seQesitm").asText(""))
                                .depositMethodQesitm(item.path("depositMethodQesitm").asText(""))
                                .openDe(item.path("openDe").asText(""))
                                .updateDe(item.path("updateDe").asText(""))
                                .itemImage(item.path("itemImage").asText(""))
                                .build();

                        filteredItems.add(dto);
                    }

                    if (filteredItems.size() >= maxResults) break;
                }

                if (filteredItems.size() >= maxResults) break;
            }

            return filteredItems;

        } catch (Exception e) {
            log.error("❌ JSON 파싱 오류: {}", e.getMessage());
            return Collections.emptyList();
        }
    }


}

package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.service.KoreanMedicineService;
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
    private final KoreanMedicineService koreanMedicineService;
    public MedicineTestController(WebClient.Builder webClientBuilder, KoreanMedicineService koreanMedicineService) {
        this.webClient = webClientBuilder.build(); // ✅ baseUrl 제거 (절대 URL 사용 시)
        this.koreanMedicineService=koreanMedicineService;
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

//    @GetMapping("/search/by-category")
//    public Mono<ResponseEntity<List<KoreanMedicineResponseDTO>>> searchMedicineByCategory(
//            @RequestParam MedicineCategory category,
//            @RequestParam(defaultValue = "json") String type) throws UnsupportedEncodingException {
//
//        log.info("🔹 요청된 카테고리: {}", category);
//
//        String effectKeyword = MedicineCategoryMapper.getEffectKeywordForCategory(category);
//        boolean isOtherCategory = (category == MedicineCategory.OTHER);
//
//        List<Mono<String>> apiRequests = new ArrayList<>();
//
//        for (int i = 1; i <= 5; i++) {
//            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
//            String encodedPageNo = URLEncoder.encode(String.valueOf(i), StandardCharsets.UTF_8.toString());
//            String encodedNumOfRows = URLEncoder.encode("100", StandardCharsets.UTF_8.toString());
//            String encodedType = URLEncoder.encode(type, StandardCharsets.UTF_8.toString());
//
//            URI uri = URI.create(String.format("%s/getDrbEasyDrugList?serviceKey=%s&pageNo=%s&numOfRows=%s&type=%s",
//                    baseUrl, encodedServiceKey, encodedPageNo, encodedNumOfRows, encodedType));
//
//            log.info("🔹 요청 URI (페이지 {}): {}", i, uri);
//
//            apiRequests.add(webClient.get()
//                    .uri(uri)
//                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
//                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                    .retrieve()
//                    .bodyToMono(String.class));
//        }
//
//        return Mono.zip(apiRequests, results -> {
//            List<String> allResponses = Arrays.stream(results).map(obj -> (String) obj).toList();
//            List<KoreanMedicineResponseDTO> medicineList = filterAndConvertToDTO(allResponses, effectKeyword, isOtherCategory, category, 10);
//            return ResponseEntity.ok(medicineList);
//        }).doOnError(error -> log.error("❌ API 요청 중 오류 발생: {}", error.getMessage()));
//    }
//
//    /**
//     * 🔎 API 응답 JSON에서 특정 증상을 포함하는 데이터만 필터링 후 DTO 변환 (이미지 필터링 포함)
//     */
//    private List<KoreanMedicineResponseDTO> filterAndConvertToDTO(List<String> responses, String effectKeyword, boolean isOtherCategory, MedicineCategory category, int maxResults) {
//        try {
//            List<KoreanMedicineResponseDTO> filteredItems = new ArrayList<>();
//
//            for (String responseBody : responses) {
//                JsonNode rootNode = objectMapper.readTree(responseBody);
//                JsonNode itemsNode = rootNode.path("body").path("items");
//
//                if (!itemsNode.isArray()) continue;
//
//                Iterator<JsonNode> elements = itemsNode.elements();
//                while (elements.hasNext()) {
//                    JsonNode item = elements.next();
//                    String efcyText = item.path("efcyQesitm").asText("");
//                    String itemImage = item.path("itemImage").asText("").trim(); // ✅ 이미지 값 가져오기
//
//                    boolean matchesCategory = effectKeyword != null && Arrays.stream(effectKeyword.split(" ")).anyMatch(efcyText::contains);
//                    boolean isOther = MedicineCategoryMapper.isOtherCategory(efcyText);
//
//                    // ✅ 이미지가 없거나 비어있는 경우 제외
//                    if (itemImage.isEmpty()) {
//                        log.info("⛔ 이미지가 없는 약품 제외됨: {}", item.path("itemName").asText(""));
//                        continue;
//                    }
//
//                    if ((isOtherCategory && isOther) || (!isOtherCategory && matchesCategory)) {
//                        KoreanMedicineResponseDTO dto = KoreanMedicineResponseDTO.builder()
//                                .entpName(item.path("entpName").asText(""))
//                                .itemName(item.path("itemName").asText(""))
//                                .itemSeq(item.path("itemSeq").asText(""))
//                                .efcyQesitm(efcyText)
//                                .useMethodQesitm(item.path("useMethodQesitm").asText(""))
//                                .atpnQesitm(item.path("atpnQesitm").asText(""))
//                                .intrcQesitm(item.path("intrcQesitm").asText(""))
//                                .seQesitm(item.path("seQesitm").asText(""))
//                                .depositMethodQesitm(item.path("depositMethodQesitm").asText(""))
//                                .openDe(item.path("openDe").asText(""))
//                                .updateDe(item.path("updateDe").asText(""))
//                                .itemImage(itemImage)
//                                .category(category)
//                                .build();
//
//                        filteredItems.add(dto);
//                    }
//
//                    if (filteredItems.size() >= maxResults) break;
//                }
//
//                if (filteredItems.size() >= maxResults) break;
//            }
//
//            return filteredItems;
//
//        } catch (Exception e) {
//            log.error("❌ JSON 파싱 오류: {}", e.getMessage());
//            return Collections.emptyList();
//        }
//    }

    @GetMapping("/search/by-category")
    public Mono<ResponseEntity<List<KoreanMedicineResponseDTO>>> searchMedicineByCategory(
            @RequestParam MedicineCategory category) {

        log.info("🔹 요청된 카테고리: {}", category);

        return koreanMedicineService.getMedicinesByCategory(category)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("❌ API 요청 중 오류 발생: {}", error.getMessage()));
    }

    @PostMapping("/save/by-category")
    public Mono<ResponseEntity<String>> saveMedicineByCategory(
            @RequestParam MedicineCategory category) {

        log.info("🔹 요청된 카테고리 저장: {}", category);

        return koreanMedicineService.saveKoreanMedicinesToDB(category)
                .then(Mono.just(ResponseEntity.ok("✅ 카테고리 " + category + " 의약품 저장 완료!")));
    }

}

package com.pharmquest.pharmquest.domain.medicine.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.service.TranslationService;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MedicineRepository {

    private final WebClient webClient;
    private final TranslationService translationService;

    @Value("${fda.api.api-key}")
    private String apiKey;

    public MedicineRepository(WebClient webClient, TranslationService translationService) {
        this.webClient = webClient;
        this.translationService = translationService;
    }

    public List<MedicineResponseDTO> findMedicines(String query, int limit) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/drug/label.json")
                            .queryParam("search", query)
                            .queryParam("limit", limit)
                            .queryParam("api_key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode results = rootNode.get("results");

            List<MedicineResponseDTO> medicines = new ArrayList<>();
            if (results != null && results.isArray()) {
                for (JsonNode result : results) {
                    JsonNode openFda = result.path("openfda");

                    // 원래 이름과 번역된 이름을 조합
                    String brandName = combineWithTranslation(
                            openFda.path("brand_name").isArray()
                                    ? openFda.path("brand_name").get(0).asText("Unknown") : "Unknown");
                    String genericName = combineWithTranslation(
                            openFda.path("generic_name").isArray()
                                    ? openFda.path("generic_name").get(0).asText("Unknown") : "Unknown");

                    String substanceName = translateIfNeeded(openFda.path("substance_name").isArray()
                            ? openFda.path("substance_name").get(0).asText("Unknown") : "Unknown");
                    String activeIngredient = translateIfNeeded(result.path("active_ingredient").isArray()
                            ? result.path("active_ingredient").get(0).asText("Unknown") : "Unknown");
                    String route = translateIfNeeded(openFda.path("route").isArray()
                            ? openFda.path("route").get(0).asText("Unknown") : "Unknown");
                    String purpose = translateIfNeeded(result.path("purpose").isArray()
                            ? result.path("purpose").get(0).asText("Unknown") : "Unknown");
                    String indications = translateIfNeeded(result.path("indications_and_usage").isArray()
                            ? result.path("indications_and_usage").get(0).asText("Unknown") : "Unknown");
                    String dosageAndAdministration = translateIfNeeded(result.path("dosage_and_administration").isArray()
                            ? result.path("dosage_and_administration").get(0).asText("Unknown") : "Unknown");
                    String dosageFormsAndStrengths = translateIfNeeded(result.path("dosage_forms_and_strengths").isArray()
                            ? result.path("dosage_forms_and_strengths").get(0).asText("Unknown") : "Unknown");
                    String splSetId = openFda.path("spl_set_id").isArray()
                            ? openFda.path("spl_set_id").get(0).asText("Unknown") : "Unknown";

                    medicines.add(new MedicineResponseDTO(
                            brandName,
                            genericName,
                            substanceName,
                            activeIngredient,
                            route,
                            purpose,
                            indications,
                            dosageAndAdministration,
                            dosageFormsAndStrengths,
                            splSetId
                    ));
                }
            }
            return medicines;

        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }

    /**
     * 원래 텍스트와 번역된 텍스트를 결합하여 반환.
     */
    private String combineWithTranslation(String text) {
        try {
            String translated = translationService.translateText(text, "ko");
            return text + " / " + translated; // 원래 텍스트와 번역된 텍스트를 슬래시로 구분
        } catch (Exception e) {
            return text; // 번역 실패 시 원래 텍스트 반환
        }
    }

    /**
     * 텍스트를 번역하는 메서드
     */
    private String translateIfNeeded(String text) {
        try {
            return translationService.translateText(text, "ko"); // 영어에서 한국어로 번역
        } catch (Exception e) {
            return text; // 번역 실패 시 원래 텍스트 반환
        }
    }


    public String findTotal(String query, int limit) {
        try {
            // FDA API 호출
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/drug/label.json")
                            .queryParam("search", query)
                            .queryParam("limit", limit)
                            .queryParam("api_key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 원본 JSON 응답을 반환
            return response;
        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }
}

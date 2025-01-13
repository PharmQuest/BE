package com.pharmquest.pharmquest.domain.medicine.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MedicineRepository {

    private final WebClient webClient;

    @Value("${fda.api.api-key}")
    private String apiKey;

    public MedicineRepository(WebClient webClient) {
        this.webClient = webClient;
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
                    medicines.add(new MedicineResponseDTO(
                            openFda.path("brand_name").isArray() ? openFda.path("brand_name").get(0).asText("Unknown") : "Unknown",
                            openFda.path("generic_name").isArray() ? openFda.path("generic_name").get(0).asText("Unknown") : "Unknown",
                            openFda.path("substance_name").isArray() ? openFda.path("substance_name").get(0).asText("Unknown") : "Unknown",
                            result.path("active_ingredient").isArray() ? result.path("active_ingredient").get(0).asText("Unknown") : "Unknown",
                            openFda.path("route").isArray() ? openFda.path("route").get(0).asText("Unknown") : "Unknown",
                            result.path("purpose").isArray() ? result.path("purpose").get(0).asText("Unknown") : "Unknown",
                            result.path("indications_and_usage").isArray() ? result.path("indications_and_usage").get(0).asText("Unknown") : "Unknown",
                            result.path("dosage_and_administration").isArray() ? result.path("dosage_and_administration").get(0).asText("Unknown") : "Unknown",
                            result.path("dosage_forms_and_strengths").isArray() ? result.path("dosage_forms_and_strengths").get(0).asText("Unknown") : "Unknown"
                    ));
                }
            }
            return medicines;

        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }
}


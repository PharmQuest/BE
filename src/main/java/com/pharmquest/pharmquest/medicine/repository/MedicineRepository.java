package com.pharmquest.pharmquest.medicine.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.medicine.dto.MedicineResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
                    medicines.add(new MedicineResponseDTO(
                            result.path("effective_time").asText("N/A"),
                            result.path("purpose").toString(),
                            result.path("warnings").toString(),
                            result.path("dosage_and_administration").toString(),
                            result.path("active_ingredient").toString()
                    ));
                }
            }
            return medicines;

        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }
}

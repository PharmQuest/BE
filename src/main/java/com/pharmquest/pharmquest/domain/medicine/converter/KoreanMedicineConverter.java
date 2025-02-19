package com.pharmquest.pharmquest.domain.medicine.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.web.dto.KoreanMedicineResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KoreanMedicineConverter {

    /**
     * ✅ JSON 데이터를 KoreanMedicineResponseDTO로 변환
     */
    public static KoreanMedicineResponseDTO convertToDTO(JsonNode item, MedicineCategory category) {
        String itemImage = item.path("itemImage").asText("").trim();

        // ✅ 이미지가 없는 경우 DTO 변환하지 않음
        if (itemImage.isEmpty()) {
            log.info("⛔ 이미지가 없는 약품 제외됨: {}", item.path("itemName").asText(""));
            return null;
        }

        return KoreanMedicineResponseDTO.builder()
                .entpName(item.path("entpName").asText(""))
                .itemName(item.path("itemName").asText(""))
                .itemSeq(item.path("itemSeq").asText(""))
                .efcyQesitm(item.path("efcyQesitm").asText(""))
                .useMethodQesitm(item.path("useMethodQesitm").asText(""))
                .atpnQesitm(item.path("atpnQesitm").asText(""))
                .intrcQesitm(item.path("intrcQesitm").asText(""))
                .seQesitm(item.path("seQesitm").asText(""))
                .depositMethodQesitm(item.path("depositMethodQesitm").asText(""))
                .openDe(item.path("openDe").asText(""))
                .updateDe(item.path("updateDe").asText(""))
                .itemImage(itemImage)
                .category(category)
                .build();
    }
}

package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.converter.KoreanMedicineConverter;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.repository.KoreanMedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.web.dto.KoreanMedicineResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KoreanMedicineService {

    private final KoreanMedicineRepository koreanMedicineRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<List<KoreanMedicineResponseDTO>> getMedicinesByCategory(MedicineCategory category) {
        String effectKeyword = MedicineCategoryMapper.getEffectKeywordForCategory(category);
        boolean isOtherCategory = (category == MedicineCategory.OTHER);

        return koreanMedicineRepository.fetchMedicineData().map(responses -> {
            List<KoreanMedicineResponseDTO> filteredItems = new ArrayList<>();

            for (String responseBody : responses) {
                try {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode itemsNode = rootNode.path("body").path("items");

                    if (!itemsNode.isArray()) continue;

                    for (JsonNode item : itemsNode) {
                        String efcyText = item.path("efcyQesitm").asText("");

                        boolean matchesCategory = effectKeyword != null && Arrays.stream(effectKeyword.split(" ")).anyMatch(efcyText::contains);
                        boolean isOther = MedicineCategoryMapper.isOtherCategory(efcyText);

                        if ((isOtherCategory && isOther) || (!isOtherCategory && matchesCategory)) {
                            KoreanMedicineResponseDTO dto = KoreanMedicineConverter.convertToDTO(item, category);
                            if (dto != null) {
                                filteredItems.add(dto);
                            }
                        }

                        if (filteredItems.size() >= 10) break;
                    }

                    if (filteredItems.size() >= 10) break;

                } catch (Exception e) {
                    log.error("❌ JSON 파싱 오류: {}", e.getMessage());
                }
            }

            return filteredItems;
        });
    }
}

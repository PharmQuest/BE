package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.converter.KoreanMedicineConverter;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.repository.KoreanMedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.web.dto.KoreanMedicineResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.UnifiedMedicineDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KoreanMedicineService {

    private final KoreanMedicineRepository koreanMedicineRepository;
    private final MedRepository medRepository;
    private final KoreanMedicineConverter koreanMedicineConverter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ✅ 특정 카테고리의 한국 약 데이터를 가져옴
     */
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

                        boolean matchesCategory = effectKeyword != null && efcyText.contains(effectKeyword);
                        boolean isOther = MedicineCategoryMapper.isOtherCategory(efcyText);

                        if ((isOtherCategory && isOther) || (!isOtherCategory && matchesCategory)) {
                            KoreanMedicineResponseDTO dto = koreanMedicineConverter.convertToDTO(item, category);
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

    public Mono<Void> saveKoreanMedicinesToDB(MedicineCategory category) {
        return koreanMedicineRepository.fetchMedicineData()
                .flatMapIterable(responses -> {
                    List<Medicine> medicineList = new ArrayList<>();
                    for (String responseBody : responses) {
                        try {
                            JsonNode rootNode = objectMapper.readTree(responseBody);
                            JsonNode itemsNode = rootNode.path("body").path("items");

                            if (!itemsNode.isArray()) continue;

                            for (JsonNode item : itemsNode) {
                                KoreanMedicineResponseDTO dto = koreanMedicineConverter.convertToDTO(item, category);
                                if (dto != null) {
                                    medicineList.add(koreanMedicineConverter.convertToMedicineEntity(dto));
                                }
                            }
                        } catch (Exception e) {
                            log.error("❌ JSON 파싱 오류: {}", e.getMessage());
                        }
                    }
                    return medicineList;
                })
                .doOnNext(medicine -> {
                    medRepository.save(medicine); // ✅ 개별 저장
                    medRepository.flush(); // ✅ 즉시 DB 반영
                })
                .then();
    }

}

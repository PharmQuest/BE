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
                    int savedCount = 0;

                    // ✅ 카테고리별 검색 키워드 가져오기
                    String effectKeyword = MedicineCategoryMapper.getEffectKeywordForCategory(category);

                    for (String responseBody : responses) {
                        try {
                            JsonNode rootNode = objectMapper.readTree(responseBody);
                            JsonNode itemsNode = rootNode.path("body").path("items");

                            if (!itemsNode.isArray()) continue;

                            for (JsonNode item : itemsNode) {
                                if (savedCount >= 15) break; // ✅ 최대 15개 저장

                                KoreanMedicineResponseDTO dto = koreanMedicineConverter.convertToDTO(item, category);
                                if (dto == null) continue;

                                // ✅ 필터링: `efcyQesitm`(효능)에 해당 카테고리 키워드 포함 여부 확인
                                String efcyText = item.path("efcyQesitm").asText("");
                                boolean matchesCategory = effectKeyword != null && efcyText.contains(effectKeyword);

                                if (matchesCategory) {
                                    // ✅ 유효성 검사: 모든 필드가 올바르게 입력된 경우만 저장
                                    if (!isValidKoreanMedicine(dto)) {
                                        log.info("⏭ 유효하지 않은 약품 제외됨: {}", dto.getItemName());
                                        continue;
                                    }

                                    // ✅ 중복 방지: 같은 `splSetId`가 이미 존재하는지 확인
                                    if (medRepository.existsBySplSetId(dto.getItemSeq())) {
                                        log.info("⏭ 이미 존재하는 약품 (중복 저장 방지): {}", dto.getItemName());
                                        continue;
                                    }

                                    Medicine medicine = koreanMedicineConverter.convertToMedicineEntity(dto);
                                    medicineList.add(medicine);
                                    savedCount++;
                                }
                            }
                        } catch (Exception e) {
                            log.error("❌ JSON 파싱 오류: {}", e.getMessage());
                        }

                        if (savedCount >= 15) break; // ✅ 15개 이상 저장 시 종료
                    }

                    return medicineList;
                })
                .take(15)
                .doOnNext(medRepository::save)  // ✅ 개별 저장
                .then();
    }



    private boolean isValidKoreanMedicine(KoreanMedicineResponseDTO dto) {
        return isValid(dto.getItemName()) &&
                isValid(dto.getEfcyQesitm()) &&
                isValid(dto.getUseMethodQesitm()) &&
                isValid(dto.getAtpnQesitm()) &&
                isValid(dto.getIntrcQesitm()) &&
                isValid(dto.getSeQesitm()) &&
                isValid(dto.getDepositMethodQesitm()) &&
                isValid(dto.getOpenDe()) &&
                isValid(dto.getUpdateDe()) &&
                isValid(dto.getItemImage());
    }

    private boolean isValid(String value) {
        return value != null && !value.isEmpty() && !value.equalsIgnoreCase("Unknown");
    }


}

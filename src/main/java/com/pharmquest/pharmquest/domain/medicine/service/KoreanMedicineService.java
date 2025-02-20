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
//    public Mono<List<KoreanMedicineResponseDTO>> getMedicinesByCategory(MedicineCategory category) {
//        String effectKeyword = MedicineCategoryMapper.getEffectKeywordForCategory(category);
//        boolean isOtherCategory = (category == MedicineCategory.OTHER);
//
//        return koreanMedicineRepository.fetchMedicineData().map(responses -> {
//            List<KoreanMedicineResponseDTO> filteredItems = new ArrayList<>();
//
//            for (String responseBody : responses) {
//                try {
//                    JsonNode rootNode = objectMapper.readTree(responseBody);
//                    JsonNode itemsNode = rootNode.path("body").path("items");
//
//                    if (!itemsNode.isArray()) continue;
//
//                    for (JsonNode item : itemsNode) {
//                        String efcyText = item.path("efcyQesitm").asText("");
//
//                        boolean matchesCategory = effectKeyword != null && efcyText.contains(effectKeyword);
//                        boolean isOther = MedicineCategoryMapper.isOtherCategory(efcyText);
//
//                        if ((isOtherCategory && isOther) || (!isOtherCategory && matchesCategory)) {
//                            KoreanMedicineResponseDTO dto = koreanMedicineConverter.convertToDTO(item, category);
//                            if (dto != null) {
//                                filteredItems.add(dto);
//                            }
//                        }
//
//                        if (filteredItems.size() >= 10) break;
//                    }
//
//                    if (filteredItems.size() >= 10) break;
//
//                } catch (Exception e) {
//                    log.error("❌ JSON 파싱 오류: {}", e.getMessage());
//                }
//            }
//
//            return filteredItems;
//        });
//    }

    public Mono<Void> saveKoreanMedicinesToDB(MedicineCategory category) {
        return koreanMedicineRepository.fetchMedicineData()
                .flatMapIterable(responses -> {
                    List<Medicine> medicineList = new ArrayList<>();
                    int savedCount = 0;  // 저장 개수 추적

                    for (String responseBody : responses) {
                        try {
                            JsonNode rootNode = objectMapper.readTree(responseBody);
                            JsonNode itemsNode = rootNode.path("body").path("items");

                            if (!itemsNode.isArray()) continue;

                            for (JsonNode item : itemsNode) {
                                if (savedCount >= 20) break;

                                KoreanMedicineResponseDTO dto = koreanMedicineConverter.convertToDTO(item, category);
                                if (dto != null && isValidKoreanMedicine(dto)) {  // 빈 값이 없는 데이터만 저장
                                    Medicine medicine = koreanMedicineConverter.convertToMedicineEntity(dto);
                                    medicineList.add(medicine);
                                    savedCount++;
                                }
                            }
                        } catch (Exception e) {
                            log.error("❌ JSON 파싱 오류: {}", e.getMessage());
                        }

                        if (savedCount >= 20) break;  //  15개 저장 완료되면 반복 종료
                    }

                    return medicineList;
                })
                .take(20)
                .doOnNext(medRepository::save)  // 개별 저장
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
                isValid(dto.getItemImage());
    }

    private boolean isValid(String value) {
        return value != null && !value.isEmpty() && !value.equalsIgnoreCase("Unknown");
    }


    public Mono<List<KoreanMedicineResponseDTO>> getMedicinesByKeyword(String keyword) {
        return koreanMedicineRepository.fetchMedicineDataWithKeyword(keyword).map(responses -> {
            List<KoreanMedicineResponseDTO> filteredItems = new ArrayList<>();

            for (String responseBody : responses) {
                try {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode itemsNode = rootNode.path("body").path("items");

                    if (!itemsNode.isArray()) continue;

                    for (JsonNode item : itemsNode) {
                        String efcyText = item.path("efcyQesitm").asText("");
                        String itemText = item.path("itemName").asText("");

                        if (efcyText.contains(keyword) || itemText.contains(keyword)) {
                            KoreanMedicineResponseDTO dto = KoreanMedicineResponseDTO.builder()
                                    .itemName(itemText)
                                    .efcyQesitm(efcyText)
                                    .useMethodQesitm(item.path("useMethodQesitm").asText(""))
                                    .atpnQesitm(item.path("atpnQesitm").asText(""))
                                    .intrcQesitm(item.path("intrcQesitm").asText(""))
                                    .seQesitm(item.path("seQesitm").asText(""))
                                    .depositMethodQesitm(item.path("depositMethodQesitm").asText(""))
                                    .itemImage(item.path("itemImage").asText(""))
                                    .build();

                            // ✅ null 값이 없는 데이터만 추가
                            if (isValidKoreanMedicine(dto)) {
                                filteredItems.add(dto);
                            }
                        }
                    }

                } catch (Exception e) {
                    log.error("❌ JSON 파싱 오류: {}", e.getMessage());
                }
            }

            return filteredItems;
        });
    }

    public Mono<List<KoreanMedicineResponseDTO>> getMedicinesByCategory(MedicineCategory category) {
        String keyword = MedicineCategoryMapper.getEffectKeywordForCategory(category);

        if (keyword == null || keyword.isEmpty()) {
            log.warn("❗ 카테고리에 해당하는 키워드가 없음: {}", category);
            return Mono.just(new ArrayList<>());
        }

        return getMedicinesByKeyword(keyword)
                .map(medicines -> medicines.stream().limit(10).toList()); // 최대 10개만 반환
    }


}

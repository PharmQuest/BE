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



    public Mono<Void> saveKoreanMedicinesToDB(MedicineCategory category) {
        String keyword = MedicineCategoryMapper.getEffectKeywordForCategory(category);

        if (keyword == null || keyword.isEmpty()) {
            log.warn("❗ 카테고리에 해당하는 키워드가 없음: {}", category);
            return Mono.empty();
        }

        return getMedicinesByKeyword(keyword, category)  // 🔹 DTO에 category 직접 주입
                .map(medicines -> medicines.stream()
                        .limit(10)
                        .toList())
                .flatMapMany(Flux::fromIterable)
                .map(koreanMedicineConverter::convertToMedicineEntity)
                .doOnNext(medicine -> log.info("🟢 변환된 엔티티 저장: {} (카테고리: {})", medicine.getBrandName(), medicine.getCategory())) //  로그 확인
                .collectList()
                .flatMap(medicineList -> Mono.fromCallable(() -> medRepository.saveAll(medicineList)))
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


    public Mono<List<KoreanMedicineResponseDTO>> getMedicinesByKeyword(String keyword, MedicineCategory category) {
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
                                    .category(category)  //  요청된 카테고리 그대로 설정
                                    .build();

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

        return getMedicinesByKeyword(keyword,category)
                .map(medicines -> medicines.stream()
                        .peek(medicine -> medicine.setCategory(category)) //  DTO에 카테고리 설정
                        .limit(10)
                        .toList());
    }





}

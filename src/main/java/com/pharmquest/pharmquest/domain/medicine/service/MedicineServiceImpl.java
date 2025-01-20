package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final TranslationService translationService;

    public MedicineServiceImpl(MedicineRepository medicineRepository, TranslationService translationService) {
        this.medicineRepository = medicineRepository;
        this.translationService = translationService;
    }

    //전체 정보 확인용
    //FDA API에서 약물 데이터를 가져오고, JSON 문자열을 반환합니다.
    @Override
    public String getTotalData(String query, int limit) {
        return medicineRepository.fetchMedicineData(query, limit); // 그대로 반환
    }


    //FDA API에서 약물 데이터를 가져오고 DTO로 변환합니다.
    @Override
    public List<MedicineResponseDTO> getMedicines(String query, int limit) {
        try {
            String response = medicineRepository.fetchMedicineData(query, limit);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode results = rootNode.get("results");

            List<MedicineResponseDTO> medicines = new ArrayList<>();
            if (results != null && results.isArray()) {
                for (JsonNode result : results) {
                    medicines.add(parseMedicine(result));
                }
            }
            return medicines;
        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }

    //약물 데이터를 JSON에서 파싱하여 DTO로 변환합니다.
    private MedicineResponseDTO parseMedicine(JsonNode result) {
        JsonNode openFda = result.path("openfda");

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
        String indicationsAndUsage = translateIfNeeded(result.path("indications_and_usage").isArray()
                ? result.path("indications_and_usage").get(0).asText("Unknown") : "Unknown");
        String dosageAndAdministration = translateIfNeeded(result.path("dosage_and_administration").isArray()
                ? result.path("dosage_and_administration").get(0).asText("Unknown") : "Unknown");
        String dosageFormsAndStrengths = translateIfNeeded(result.path("dosage_forms_and_strengths").isArray()
                ? result.path("dosage_forms_and_strengths").get(0).asText("Unknown") : "Unknown");
        String splSetId = openFda.path("spl_set_id").isArray()
                ? openFda.path("spl_set_id").get(0).asText("Unknown") : "Unknown";

        String imgUrl = fetchImageUrl(splSetId);

        return new MedicineResponseDTO(
                brandName,
                genericName,
                substanceName,
                activeIngredient,
                route,
                purpose,
                indicationsAndUsage,
                dosageAndAdministration,
                dosageFormsAndStrengths,
                splSetId,
                imgUrl
        );
    }

    //DailyMed API를 호출하여 SPL Set ID에 대한 이미지 URL을 가져옵니다.
    private String fetchImageUrl(String splSetId) {
        if (splSetId == null || splSetId.equals("Unknown")) {
            return null;
        }
        try {
            String mediaResponse = medicineRepository.fetchImageData(splSetId);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(mediaResponse);
            JsonNode mediaArray = rootNode.path("data").path("media");

            if (mediaArray.isArray() && mediaArray.size() > 0) {
                return mediaArray.get(0).path("url").asText();
            }
        } catch (Exception e) {
            System.err.println("이미지 URL 가져오기 실패: " + e.getMessage());
        }
        return null;
    }

    //원래 텍스트와 번역된 텍스트를 결합하여 반환합니다.
    private String combineWithTranslation(String text) {
        try {
            String translated = translationService.translateText(text, "ko");
            return text + " / " + translated;
        } catch (Exception e) {
            return text;
        }
    }

    //텍스트를 한국어로 번역합니다.
    private String translateIfNeeded(String text) {
        try {
            return translationService.translateText(text, "ko");
        } catch (Exception e) {
            return text;
        }
    }
}
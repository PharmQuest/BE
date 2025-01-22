package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
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


    //번역 전
    @Override
    public List<MedicineResponseDTO> getEnMedicines(String query, int limit) {
        try {
            String response = medicineRepository.fetchMedicineData(query, limit);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode results = rootNode.get("results");

            List<MedicineResponseDTO> medicines = new ArrayList<>();
            if (results != null && results.isArray()) {
                for (JsonNode result : results) {
                    medicines.add(parseMedicineWithoutTranslation(result));
                }
            }
            return medicines;
        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }
    private MedicineResponseDTO parseMedicineWithoutTranslation(JsonNode result) {
        JsonNode openFda = result.path("openfda");

        String brandName = openFda.path("brand_name").isArray()
                ? openFda.path("brand_name").get(0).asText("Unknown") : "Unknown";
        String genericName = openFda.path("generic_name").isArray()
                ? openFda.path("generic_name").get(0).asText("Unknown") : "Unknown";
        String substanceName = openFda.path("substance_name").isArray()
                ? openFda.path("substance_name").get(0).asText("Unknown") : "Unknown";
        String activeIngredient = result.path("active_ingredient").isArray()
                ? result.path("active_ingredient").get(0).asText("Unknown") : "Unknown";
        String route = openFda.path("route").isArray()
                ? openFda.path("route").get(0).asText("Unknown") : "Unknown";
        String purpose = result.path("purpose").isArray()
                ? result.path("purpose").get(0).asText("Unknown") : "Unknown";
        String indicationsAndUsage = result.path("indications_and_usage").isArray()
                ? result.path("indications_and_usage").get(0).asText("Unknown") : "Unknown";
        String dosageAndAdministration = result.path("dosage_and_administration").isArray()
                ? result.path("dosage_and_administration").get(0).asText("Unknown") : "Unknown";
        String dosageFormsAndStrengths = result.path("dosage_forms_and_strengths").isArray()
                ? result.path("dosage_forms_and_strengths").get(0).asText("Unknown") : "Unknown";
        String splSetId = openFda.path("spl_set_id").isArray()
                ? openFda.path("spl_set_id").get(0).asText("Unknown") : "Unknown";

        String imgUrl = fetchImageUrl(splSetId);
        String category = MedicineCategoryMapper.getCategory(purpose, activeIngredient, "", route);

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
                imgUrl,
                category
        );
    }

    @Override
    public MedicineResponseDTO getMedicineBySplSetId(String splSetId) {
        try {
            // spl_set_id로 검색 요청
            String query = "openfda.spl_set_id:" + splSetId;
            String response = medicineRepository.fetchMedicineData(query, 1); // limit 고정

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode results = rootNode.path("results");

            if (results.isArray() && results.size() > 0) {
                JsonNode result = results.get(0); // 첫 번째 결과만 사용
                return parseMedicine(result);
            } else {
                throw new IllegalArgumentException("해당 spl_set_id를 가진 약물이 없습니다: " + splSetId);
            }
        } catch (Exception e) {
            throw new RuntimeException("spl_set_id로 약물 검색 중 오류 발생", e);
        }
    }

    //약물 데이터를 JSON에서 파싱하여 DTO로 변환합니다.
    private MedicineResponseDTO parseMedicine(JsonNode result) {
        JsonNode openFda = result.path("openfda");

        // 번역 전 원본 데이터 추출
        String brandName = openFda.path("brand_name").isArray()
                ? openFda.path("brand_name").get(0).asText("Unknown") : "Unknown";
        String genericName = openFda.path("generic_name").isArray()
                ? openFda.path("generic_name").get(0).asText("Unknown") : "Unknown";
        String substanceName = openFda.path("substance_name").isArray()
                ? openFda.path("substance_name").get(0).asText("Unknown") : "Unknown";
        String activeIngredient = result.path("active_ingredient").isArray()
                ? result.path("active_ingredient").get(0).asText("Unknown") : "Unknown";
        String route = openFda.path("route").isArray()
                ? openFda.path("route").get(0).asText("Unknown") : "Unknown";
        String purpose = result.path("purpose").isArray()
                ? result.path("purpose").get(0).asText("Unknown") : "Unknown";

        // 카테고리 계산 (번역 전에 수행)
        String category = MedicineCategoryMapper.getCategory(purpose, activeIngredient, "", route);

        // 번역 수행
        String translatedBrandName = combineWithTranslation(brandName);
        String translatedGenericName = combineWithTranslation(genericName);
        String translatedSubstanceName = translateIfNeeded(substanceName);
        String translatedActiveIngredient = translateIfNeeded(activeIngredient);
        String translatedRoute = translateIfNeeded(route);
        String translatedPurpose = translateIfNeeded(purpose);
        String translatedIndicationsAndUsage = translateIfNeeded(
                result.path("indications_and_usage").isArray()
                        ? result.path("indications_and_usage").get(0).asText("Unknown")
                        : result.path("indications_and_usage").asText("Unknown")
        );
        String translatedDosageAndAdministration = translateIfNeeded(
                result.path("dosage_and_administration").isArray()
                        ? result.path("dosage_and_administration").get(0).asText("Unknown")
                        : result.path("dosage_and_administration").asText("Unknown")
        );
        String translatedDosageFormsAndStrengths = translateIfNeeded(
                result.path("dosage_forms_and_strengths").isArray()
                        ? result.path("dosage_forms_and_strengths").get(0).asText("Unknown")
                        : result.path("dosage_forms_and_strengths").asText("Unknown")
        );
        String splSetId = openFda.path("spl_set_id").isArray()
                ? openFda.path("spl_set_id").get(0).asText("Unknown") : "Unknown";

        String imgUrl = fetchImageUrl(splSetId);

        // DTO 생성
        return new MedicineResponseDTO(
                translatedBrandName,
                translatedGenericName,
                translatedSubstanceName,
                translatedActiveIngredient,
                translatedRoute,
                translatedPurpose,
                translatedIndicationsAndUsage,
                translatedDosageAndAdministration,
                translatedDosageFormsAndStrengths,
                splSetId,
                imgUrl,
                category
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
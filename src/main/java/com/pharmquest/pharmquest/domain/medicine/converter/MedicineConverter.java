package com.pharmquest.pharmquest.domain.medicine.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.service.TranslationService;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MedicineConverter {

    private final TranslationService translationService;
    private final MedicineRepository medicineRepository;

    public MedicineConverter(TranslationService translationService, MedicineRepository medicineRepository) {
        this.translationService = translationService;
        this.medicineRepository = medicineRepository;
    }

    // 번역 포함 변환
    public MedicineResponseDTO convertWithTranslation(JsonNode result) {
        String brandName = translate(getFirstValue(result, "openfda.brand_name"));
        String genericName = translate(getFirstValue(result, "openfda.generic_name"));
        String substanceName = translate(getFirstValue(result, "openfda.substance_name"));
        String activeIngredient = translate(getFirstValue(result, "active_ingredient"));
        String route = translate(getFirstValue(result, "openfda.route"));
        String purpose = translate(getFirstValue(result, "purpose"));

        String category = MedicineCategoryMapper.getCategory(
                getFirstValue(result, "purpose"),
                getFirstValue(result, "active_ingredient"),
                "",
                getFirstValue(result, "openfda.route")
        );

        String indicationsAndUsage = translate(getFirstValue(result, "indications_and_usage"));
        String dosageAndAdministration = translate(getFirstValue(result, "dosage_and_administration"));
        String dosageFormsAndStrengths = translate(getFirstValue(result, "dosage_forms_and_strengths"));
        String splSetId = getFirstValue(result, "openfda.spl_set_id");

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
                imgUrl,
                category
        );
    }

    // 번역 없이 변환
    public MedicineResponseDTO convertWithoutTranslation(JsonNode result) {
        String brandName = getFirstValue(result, "openfda.brand_name");
        String genericName = getFirstValue(result, "openfda.generic_name");
        String substanceName = getFirstValue(result, "openfda.substance_name");
        String activeIngredient = getFirstValue(result, "active_ingredient");
        String route = getFirstValue(result, "openfda.route");
        String purpose = getFirstValue(result, "purpose");

        String category = MedicineCategoryMapper.getCategory(purpose, activeIngredient, "", route);

        String indicationsAndUsage = getFirstValue(result, "indications_and_usage");
        String dosageAndAdministration = getFirstValue(result, "dosage_and_administration");
        String dosageFormsAndStrengths = getFirstValue(result, "dosage_forms_and_strengths");
        String splSetId = getFirstValue(result, "openfda.spl_set_id");

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
                imgUrl,
                category
        );
    }

    private String getFirstValue(JsonNode node, String path) {
        JsonNode valueNode = node.at("/" + path.replace('.', '/'));
        return valueNode.isArray() ? valueNode.get(0).asText("Unknown") : valueNode.asText("Unknown");
    }

    private String translate(String text) {
        try {
            return translationService.translateText(text, "ko");
        } catch (Exception e) {
            return text; // 번역 실패 시 원본 반환
        }
    }

    private String fetchImageUrl(String splSetId) {
        if (splSetId == null || splSetId.equals("Unknown")) {
            return null;
        }
        try {
            String mediaResponse = medicineRepository.fetchImageData(splSetId);
            JsonNode rootNode = new ObjectMapper().readTree(mediaResponse);
            JsonNode mediaArray = rootNode.path("data").path("media");

            if (mediaArray.isArray() && mediaArray.size() > 0) {
                return mediaArray.get(0).path("url").asText();
            }
        } catch (Exception e) {
            System.err.println("이미지 URL 가져오기 실패: " + e.getMessage());
        }
        return null;
    }
}
package com.pharmquest.pharmquest.domain.medicine.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineScrapRepository;
import com.pharmquest.pharmquest.domain.medicine.service.TranslationService;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineDetailResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineOpenapiResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineSaveDetailResponseDTO;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class MedicineConverter {

    private final TranslationService translationService;
    private final MedicineRepository medicineRepository;
    private final MedicineScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final MedRepository medRepository;


    public MedicineConverter(TranslationService translationService, MedicineRepository medicineRepository, MedicineScrapRepository scrapRepository, UserRepository userRepository, MedRepository medRepository) {
        this.translationService = translationService;
        this.medicineRepository = medicineRepository;
        this.scrapRepository = scrapRepository;
        this.userRepository = userRepository;
        this.medRepository = medRepository;
    }

    // 번역 포함 변환
    public MedicineOpenapiResponseDTO convertWithTranslation(JsonNode result) {
        String brandName = translate(getFirstValue(result, "openfda.brand_name"));
        String genericName = translate(getFirstValue(result, "openfda.generic_name"));
        MedicineCategory categoryEnum = MedicineCategoryMapper.getCategory(
                getFirstValue(result, "purpose"),
                getFirstValue(result, "active_ingredient"),
                "",
                getFirstValue(result, "openfda.route")
        );
        String category = MedicineCategoryMapper.toKoreanCategory(categoryEnum);


        String splSetId = getFirstValue(result, "openfda.spl_set_id");
        String imgUrl = fetchImageUrl(splSetId);
        String country = "미국";

        return new MedicineOpenapiResponseDTO(
                brandName,
                genericName,
                splSetId,
                imgUrl,
                category,
                country
        );
    }

    public MedicineDetailResponseDTO convertToDetail(JsonNode result, Long userId) {
        String brandName = translate(getFirstValue(result, "openfda.brand_name"));
        String genericName = translate(getFirstValue(result, "openfda.generic_name"));
        MedicineCategory categoryEnum = MedicineCategoryMapper.getCategory(
                getFirstValue(result, "purpose"),
                getFirstValue(result, "active_ingredient"),
                "",
                getFirstValue(result, "openfda.route")
        );
        String category = MedicineCategoryMapper.toKoreanCategory(categoryEnum);
        String substanceName = translate(getFirstValue(result, "openfda.substance_name"));
        String activeIngredient = translate(getFirstValue(result, "active_ingredient"));
        String purpose = translate(getFirstValue(result, "purpose"));
        String indicationsAndUsage = translate(getFirstValue(result, "indications_and_usage"));
        String dosageAndAdministration = translate(getFirstValue(result, "dosage_and_administration"));

        String splSetId = getFirstValue(result, "openfda.spl_set_id");
        String imgUrl = fetchImageUrl(splSetId);
        String country = "미국";
        String warnings = translate(getFirstValue(result, "warnings"));

        boolean isScrapped = false;
        if (userId != null) {
            isScrapped = scrapRepository.existsByUserAndMedicine(
                    userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다.")),
                    medRepository.findBySplSetId(splSetId).orElse(null)
            );
        }

        return new MedicineDetailResponseDTO(
                brandName,
                genericName,
                substanceName,
                activeIngredient,
                purpose,
                indicationsAndUsage,
                dosageAndAdministration,
                splSetId,
                imgUrl,
                category,
                country,
                warnings,
                isScrapped
        );
    }

    public MedicineSaveDetailResponseDTO SaveConvertToDetail(JsonNode result, Long userId) {
        String brandName = translate(getFirstValue(result, "openfda.brand_name"));
        String genericName = translate(getFirstValue(result, "openfda.generic_name"));
        MedicineCategory categoryEnum = MedicineCategoryMapper.getCategory(
                getFirstValue(result, "purpose"),
                getFirstValue(result, "active_ingredient"),
                "",
                getFirstValue(result, "openfda.route")
        );
        String category = MedicineCategoryMapper.toKoreanCategory(categoryEnum);
        String substanceName = translate(getFirstValue(result, "openfda.substance_name"));
        String activeIngredient = translate(getFirstValue(result, "active_ingredient"));
        String purpose = translate(getFirstValue(result, "purpose"));
        String indicationsAndUsage = translate(getFirstValue(result, "indications_and_usage"));
        String dosageAndAdministration = translate(getFirstValue(result, "dosage_and_administration"));

        String splSetId = getFirstValue(result, "openfda.spl_set_id");
        String imgUrl = fetchImageUrl(splSetId);
        String country = "미국";
        String warnings = translate(getFirstValue(result, "warnings"));



        return new MedicineSaveDetailResponseDTO(
                brandName,
                genericName,
                substanceName,
                activeIngredient,
                purpose,
                indicationsAndUsage,
                dosageAndAdministration,
                splSetId,
                imgUrl,
                category,
                country,
                warnings
        );
    }



    // 번역 없이 변환
    public MedicineOpenapiResponseDTO  convertWithoutTranslation(JsonNode result) {
        String brandName = getFirstValue(result, "openfda.brand_name");
        String genericName = getFirstValue(result, "openfda.generic_name");
        String activeIngredient = getFirstValue(result, "active_ingredient");
        String route = getFirstValue(result, "openfda.route");
        String purpose = getFirstValue(result, "purpose");
        MedicineCategory categoryEnum = MedicineCategoryMapper.getCategory(purpose, activeIngredient, "", route);
        String splSetId = getFirstValue(result, "openfda.spl_set_id");
        String imgUrl = fetchImageUrl(splSetId);
        String country = "미국";
        String category = MedicineCategoryMapper.toKoreanCategory(categoryEnum);
        return new MedicineOpenapiResponseDTO (
                brandName,
                genericName,
                splSetId,
                imgUrl,
                category,
                country
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

    public MedicineResponseDTO convertFromEntity(Medicine medicine, Long userId) {
        if (medicine == null) {
            return null;
        }
        String category = MedicineCategoryMapper.toKoreanCategory(medicine.getCategory());

        boolean isScrapped = false;
        if (userId != null) {
            isScrapped = scrapRepository.existsByUserAndMedicine(
                    userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다.")),
                    medicine
            );
        }
        return new MedicineResponseDTO(
                medicine.getId(),
                medicine.getBrandName(),
                medicine.getGenericName(),
                medicine.getSplSetId(),
                medicine.getImgUrl(),
                category,
                medicine.getCountry(),
                isScrapped
        );
    }
}

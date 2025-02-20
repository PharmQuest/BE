package com.pharmquest.pharmquest.domain.medicine.converter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.web.dto.KoreanMedicineResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KoreanMedicineConverter {

    private final AmazonS3 amazonS3;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * JSON 데이터를 KoreanMedicineResponseDTO로 변환 (S3 URL 적용)
     */
    public KoreanMedicineResponseDTO convertToDTO(JsonNode item, MedicineCategory category) {
        String itemImage = item.path("itemImage").asText("").trim();

        // 이미지가 없으면 DTO 변환하지 않음
        if (itemImage.isEmpty()) {
            log.info("⛔ 이미지가 없는 약품 제외됨: {}", item.path("itemName").asText(""));
            return null;
        }

        // S3에 업로드 후 새로운 URL 사용
        String s3ImageUrl = uploadImageToS3(itemImage);

        return KoreanMedicineResponseDTO.builder()
                .itemName(item.path("itemName").asText(""))
                .itemSeq(item.path("itemSeq").asText(""))
                .efcyQesitm(item.path("efcyQesitm").asText(""))
                .useMethodQesitm(item.path("useMethodQesitm").asText(""))
                .atpnQesitm(item.path("atpnQesitm").asText(""))
                .intrcQesitm(item.path("intrcQesitm").asText(""))
                .seQesitm(item.path("seQesitm").asText(""))
                .depositMethodQesitm(item.path("depositMethodQesitm").asText(""))
                .itemImage(s3ImageUrl) //  변환된 S3 URL 저장
                .category(category) //  조회 시 사용한 카테고리 설정
                .build();
    }


    public Medicine convertToMedicineEntity(KoreanMedicineResponseDTO dto) {

        String s3ImageUrl = uploadImageToS3(dto.getItemImage());

        Medicine medicine = new Medicine();
        medicine.setBrandName(dto.getItemName());
        medicine.setGenericName("-");
        medicine.setPurpose(removeNewLines(dto.getEfcyQesitm()));
        medicine.setIndicationsAndUsage(removeNewLines(dto.getDepositMethodQesitm()));
        medicine.setDosageAndAdministration(removeNewLines(dto.getUseMethodQesitm()));
        medicine.setSplSetId(dto.getItemSeq() != null ? dto.getItemSeq() : "-");
        medicine.setImgUrl(s3ImageUrl);

        // ✅ DTO에서 가져온 카테고리를 그대로 저장
        MedicineCategory category = dto.getCategory();
        if (category == null || category == MedicineCategory.OTHER) {
            log.warn("❗ DTO 카테고리가 OTHER 또는 NULL -> 올바른 값 설정 시도: {}", dto.getEfcyQesitm());
            category = MedicineCategoryMapper.getCategory(dto.getEfcyQesitm(), "-", "-", "-");
        }

        medicine.setCategory(category);
        medicine.setCountry("KOREA");
        medicine.setWarnings(removeNewLines(dto.getAtpnQesitm()));
        medicine.setSubstanceName("-");
        medicine.setActiveIngredient("-");

        log.info("🟢 저장되는 Medicine 엔티티: {} (카테고리: {})", medicine.getBrandName(), medicine.getCategory());
        return medicine;
    }



    private String removeNewLines(String input) {
        return input != null ? input.replace("\n", "").trim() : "";
    }


    /**
     * 이미지를 S3에 업로드 후 S3 URL 반환
     */
    private String uploadImageToS3(String imageUrl) {
        try {
            byte[] imageBytes = restTemplate.getForObject(new URL(imageUrl).toURI(), byte[].class);
            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("이미지 다운로드 실패: " + imageUrl);
            }

            String fileName = "medicine-images/" + UUID.randomUUID() + ".jpg";
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageBytes.length);
            metadata.setContentType("image/jpeg");

            amazonS3.putObject(bucketName, fileName, inputStream, metadata);

            return amazonS3.getUrl(bucketName, fileName).toString(); // 업로드된 S3 URL 반환
        } catch (Exception e) {
            log.error(" S3 업로드 실패, 기존 이미지 URL 사용: {}", imageUrl);
            return imageUrl; // S3 업로드 실패 시 기존 URL 유지
        }
    }
}

package com.pharmquest.pharmquest.domain.medicine.converter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
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
                .entpName(item.path("entpName").asText(""))
                .itemName(item.path("itemName").asText(""))
                .itemSeq(item.path("itemSeq").asText(""))
                .efcyQesitm(item.path("efcyQesitm").asText(""))
                .useMethodQesitm(item.path("useMethodQesitm").asText(""))
                .atpnQesitm(item.path("atpnQesitm").asText(""))
                .intrcQesitm(item.path("intrcQesitm").asText(""))
                .seQesitm(item.path("seQesitm").asText(""))
                .depositMethodQesitm(item.path("depositMethodQesitm").asText(""))
                .openDe(item.path("openDe").asText(""))
                .updateDe(item.path("updateDe").asText(""))
                .itemImage(s3ImageUrl) // 변환된 S3 URL 적용
                .category(category)
                .build();
    }

    public Medicine convertToMedicineEntity(KoreanMedicineResponseDTO dto) {
        Medicine medicine = new Medicine();
        medicine.setBrandName(dto.getItemName());  //  제품명
        medicine.setGenericName("-");  //
        medicine.setPurpose(dto.getEfcyQesitm());  //  효능/효과
        medicine.setIndicationsAndUsage(dto.getEfcyQesitm());  // 효능/효과
        medicine.setDosageAndAdministration(dto.getUseMethodQesitm());  // 사용법
        medicine.setSplSetId(dto.getItemSeq());  // 품목 일련번호
        medicine.setImgUrl(dto.getItemImage());  // 이미지 URL
        medicine.setCategory(dto.getCategory());  // 카테고리
        medicine.setCountry("KOREA");  // 국가는 한국으로 설정
        medicine.setWarnings(dto.getAtpnQesitm());  // 주의사항

        // 한국 의약품에는 없는 필드 하이픈 표시
        medicine.setSubstanceName("-");
        medicine.setActiveIngredient("-");

        return medicine;
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

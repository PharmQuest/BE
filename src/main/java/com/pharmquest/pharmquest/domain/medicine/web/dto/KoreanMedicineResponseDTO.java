package com.pharmquest.pharmquest.domain.medicine.web.dto;

import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KoreanMedicineResponseDTO {
    private String entpName;        // 제조사 이름
    private String itemName;        // 제품명
    private String itemSeq;         // 품목 일련번호
    private String efcyQesitm;      // 효능/효과
    private String useMethodQesitm; // 사용법
    private String atpnQesitm;      // 주의사항
    private String intrcQesitm;     // 상호작용 정보
    private String seQesitm;        // 부작용 정보
    private String depositMethodQesitm; // 보관 방법
    private String itemImage;       // 이미지 URL
    private MedicineCategory category;
}

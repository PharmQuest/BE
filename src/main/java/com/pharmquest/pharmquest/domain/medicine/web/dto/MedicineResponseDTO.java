package com.pharmquest.pharmquest.domain.medicine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicineResponseDTO {
    private String brandName; // openfda.brand_name
    private String genericName; // openfda.generic_name
    private String splSetId; //이미지 찾는 기준이 되는 약 코드
    private String imgUrl; //
    private String category;
    private String country;
}

package com.pharmquest.pharmquest.domain.medicine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicineResponseDTO {
    private String brandName; // openfda.brand_name
    private String genericName; // openfda.generic_name
    private String substanceName; // openfda.substance_name
    private String activeIngredient; // active_ingredient
    private String route; // openfda.route
    private String purpose; // purpose
    private String indicationsAndUsage; // indications_and_usage
    private String dosageAndAdministration; // dosage_and_administration
    private String dosageFormsAndStrengths; // dosage_forms_and_strengths
    private String  splSetId; //이미지 찾는 기준이 되는 약 코드
    private String imgUrl; //
    private String category;


}

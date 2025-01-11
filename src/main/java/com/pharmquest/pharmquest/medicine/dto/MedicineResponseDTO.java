package com.pharmquest.pharmquest.medicine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicineResponseDTO {
    private String effectiveTime;
    private String purpose;
    private String warnings;
    private String dosageAndAdministration;
    private String activeIngredients;
}
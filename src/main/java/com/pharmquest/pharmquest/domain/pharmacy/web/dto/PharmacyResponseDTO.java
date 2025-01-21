package com.pharmquest.pharmquest.domain.pharmacy.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PharmacyResponseDTO {
    @Builder
    @Getter
    public static class findPharmacyDto{
        private List<String> placeIds;
    }
}

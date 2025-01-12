package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SupplementsResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsDto {
        private String name;
        private String image;
        private String brand;
        private String category1;
        private String category2;
        private String category3;
        private String category4;
    }
}
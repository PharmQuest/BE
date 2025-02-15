package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class AdResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdResponseDto {
        private String type;
        private Long id;
        private String name;
        private String productName;
        private String image;
        private boolean isAd = true;
    }
}
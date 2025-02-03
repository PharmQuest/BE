package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdResponseDto {
        private Long id;
        private String smallImageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdLargeResponseDto {
        private Long id;
        private String largeImageUrl;
    }
}

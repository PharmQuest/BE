package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SupplementsResponseDTO {
    // 내부 처리용 (전체 로드)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsInternalDto {
        private String name;
        private String image;
        private String brand;
        private String maker;
        private String link;
        private String category1;
        private String category2;
        private String category3;
        private String category4;
    }

    // 응답용 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsDto {
        private String name;
        private String image;
        private String brand;
        private boolean isScrapped;
        private int scrapCount;
        private String category4;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsSearchResponseDto {
        private String name;
        private String image;
        private String brand;
        private boolean isScrapped;
        private int scrapCount;
        private String category4;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsDetailResponseDto {
        private String name;
        private String image;
        private String brand;
        private String maker;
        private String link;
        private boolean isScrapped;
        private int scrapCount;
        private String category1;
        private String category2;
        private String category3;
        private String category4;
    }
}
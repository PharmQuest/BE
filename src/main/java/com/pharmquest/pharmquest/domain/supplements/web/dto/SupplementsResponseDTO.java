package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
        private String category1;
        private String category2;
        private String category3;
        private String category4;
        private String link;
    }

    // 응답용 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsDto {
        private Long id;
        private String name;
        private String image;
        private String brand;
        private boolean isScrapped;
        private int scrapCount;
        private String category4;
        private List<String> categories;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class SupplementsPageResponseDto {
        private int amountPage;
        private int amountCount;
        private int currentPage;
        private int currentCount;
        private AdResponseDTO.AdResponseDto adResponse;
        private List<SupplementsDto> supplements;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsSearchResponseDto {
        private Long id;
        private String name;
        private String image;
        private String brand;
        private boolean isScrapped;
        private int scrapCount;
        private int amountPage;
        private int amountCount;
        private String category4;
        private List<String> categories;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class SupplementsSearchPageResponseDto {
        private int amountPage;
        private int amountCount;
        private int currentPage;
        private int currentCount;
        private AdResponseDTO.AdResponseDto adResponse;
        private List<SupplementsSearchResponseDto> supplements;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedSupplementDto {
        private Long id;
        private String name;
        private String image;
        private String brand;
        private String maker;
        private boolean isScrapped;
        private int scrapCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsDetailResponseDto {
        private Long id;
        private String name;
        private String image;
        private String brand;
        private String maker;
        private boolean isScrapped;
        private int scrapCount;
        private String category1;
        private String category2;
        private String category3;
        private String category4;
        private String dosage;
        private String purpose;
        private String warning;
        private List<String> categories;
        private List<RelatedSupplementDto> relatedSupplements;
    }
}


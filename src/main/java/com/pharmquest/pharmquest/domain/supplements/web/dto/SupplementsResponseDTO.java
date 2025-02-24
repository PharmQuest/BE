package com.pharmquest.pharmquest.domain.supplements.web.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

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
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsDto {
        private String type;
        private Long id;
        private String name;
        private String country;
        private String productName;
        private String image;
        private String brand;
        private boolean isScrapped;
        private int scrapCount;
        private String category4;
        private List<String> categories;
        private List<String> selectCategories;
        private boolean isAd = false;
    }

    //전체 리스트용 dto
    @Builder
    @Getter
    @AllArgsConstructor
    public static class SupplementsPageResponseDto {
        private int amountPage;
        private int amountCount;
        private int currentPage;
        private int currentCount;
        private List<SupplementsDto> items;
    }

    //검색 영양제용 dto
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsSearchResponseDto {
        private String type;
        private Long id;
        private String name;
        private String country;
        private String productName;
        private String image;
        private String brand;
        private boolean isScrapped;
        private int scrapCount;
        private String category4;
        private List<String> categories;
        private List<String> selectCategories;
        private boolean isAd = false;
    }

    //검색 리스트용 dto
    @Builder
    @Getter
    @AllArgsConstructor
    public static class SupplementsSearchPageResponseDto {
        private int amountPage;
        private int amountCount;
        private int currentPage;
        private int currentCount;
        private List<SupplementsSearchResponseDto> items;
    }

    //연관 영양제용 dto
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedSupplementDto {
        private Long id;
        private String name;
        private String country;
        private String productName;
        private String image;
        private String brand;
        private String maker;
        private List<String> categories;
        private List<String> selectCategories;
        private boolean isScrapped;
        private int scrapCount;
    }

    //영양제 상세정보용 dto
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsDetailResponseDto {
        private Long id;
        private String name;
        private String country;
        private String productName;
        private String image;
        private String brand;
        private String maker;
        private boolean isScrapped;
        private int scrapCount;
        private List<String> productCategory;
        private List<String> dosage;
        private List<String> purpose;
        private List<String> warning;
        private List<String> categories;
        private List<String> selectCategories;
        private List<RelatedSupplementDto> relatedSupplements;
    }
}


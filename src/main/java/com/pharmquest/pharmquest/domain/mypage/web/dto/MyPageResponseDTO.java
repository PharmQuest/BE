package com.pharmquest.pharmquest.domain.mypage.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyPageResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsResponseDto {
        private Long id;
        private String name;
        private String country;
        private String productName;
        private String image;
        private String brand;
        private boolean isScrapped;
        private List<String> categories;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineResponseDto {
        private Long id;
        private String productName;
        private String generalName;
        private String productImage;
        private String categories;
        private String country;
    }

    @Builder
    @Getter
    public static class PharmacyDto {
        private String name;
        @JsonProperty("place_id")
        private String placeId;
        private String region;
        private Double latitude;
        private Double longitude;
        @JsonProperty(value = "img_url")
        private String imgUrl;
    }

    @Builder
    @Getter
    public static class PharmacyResponse {
        private List<PharmacyDto> pharmacies;
        @JsonProperty("total_elements")
        private Integer totalElements;
        @JsonProperty("total_pages")
        private Integer totalPages;
        @JsonProperty("current_page")
        private Integer currentPage;
        @JsonProperty("elements_per_page")
        private Integer elementsPerPage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScrapPostResponseDTO {
        Long postId;
        String writerName;
        String title;
        String content;
        PostCategory category;
        Integer scrapeCount;
        Integer likeCount;
        Integer commentCount;
        LocalDate createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResponseDTO {
        Long postId;
        String title;
        String content;
        PostCategory category;
        Integer scrapeCount;
        Integer likeCount;
        Integer commentCount;
        LocalDate createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponseDTO {
        Long postId;
        Long commentId;
        String title;
        String content;
        LocalDate createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class notificationResponseDTO {
        Long postId;
        String postTitle;
        String commentWriter;
        String commentContent;
        LocalDate createdAt;
    }
}

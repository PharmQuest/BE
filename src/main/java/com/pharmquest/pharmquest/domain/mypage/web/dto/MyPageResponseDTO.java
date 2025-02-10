package com.pharmquest.pharmquest.domain.mypage.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyPageResponseDTO {

    @Getter
    public static class SupplementsResponseDto {
        private final Long id;
        private final String name;
        private final String image;
        private final List<String> categories;

        @Builder
        private SupplementsResponseDto(Long id, String name, String image, List<String> categories) {
            this.id = id;
            this.name = name;
            this.image = image;
            this.categories = categories != null ? categories : new ArrayList<>();
        }

        public static SupplementsResponseDto from(Supplements supplement) {
            if (supplement == null) {
                return null;
            }

            return SupplementsResponseDto.builder()
                    .id(supplement.getId())
                    .name(supplement.getName())
                    .image(supplement.getImage())
                    .categories(supplement.getSupplementsCategoryList().stream()
                            .map(sc -> sc.getCategory().getName())
                            .collect(Collectors.toList()))
                    .build();
        }
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
//        @JsonProperty(value = "open_now")
//        private Boolean openNow;
        private String region;
        private Double latitude;
        private Double longitude;
        private String country;
//        private List<GooglePlaceDetailsResponse.Period> periods;
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

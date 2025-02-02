package com.pharmquest.pharmquest.domain.mypage.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MyPageResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsResponseDto {
        private Long id;
        private String name;
        private String image;
        private List<String> categories;
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
        private Integer count;
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
        LocalDateTime createdAt;
    }
}

package com.pharmquest.pharmquest.domain.mypage.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pharmquest.pharmquest.domain.pharmacy.web.dto.GooglePlaceDetailsResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ScrapResponseDTO {

    @Builder
    @Getter
    public static class PharmacyDto {
        private String name;
        @JsonProperty(value = "open_now")
        private Boolean openNow;
        private String region;
        private Double latitude;
        private Double longitude;
        private String country;
        private List<GooglePlaceDetailsResponse.Period> periods;
        @JsonProperty(value = "img_url")
        private String imgUrl;
    }

    @Builder
    @Getter
    public static class PharmacyResponse {
        private List<PharmacyDto> pharmacies;
        private Integer count;
    }


}

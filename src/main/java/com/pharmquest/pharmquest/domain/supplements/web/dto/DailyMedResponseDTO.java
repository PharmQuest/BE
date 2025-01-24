package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.*;

import java.util.List;

public class DailyMedResponseDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchResponse {
        private List<SearchItemDto> data;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchItemDto {
        private int spl_version;
        private String published_date;
        private String title;
        private String setid;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class ExtractedInfo {
        private String setid;
        private String title;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailInfo {
        private String title;
        private String manufacturer;
        private String dosage;
        private String purpose;
        private String warning;
        private String imageUrl;
    }
}
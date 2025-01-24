package com.pharmquest.pharmquest.domain.mypage.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MyPageResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplementsResponseDto {
        private Long id;
        private String name;
        private String image;
    }
}

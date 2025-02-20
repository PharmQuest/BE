package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.Builder;
import lombok.Getter;

//스크랩 결과 dto
@Getter
@Builder
public class SupplementsScrapResponseDTO {
    private Long supplementId;
    private boolean isScrapped;
    private int scrapCount;
    private String message;
}
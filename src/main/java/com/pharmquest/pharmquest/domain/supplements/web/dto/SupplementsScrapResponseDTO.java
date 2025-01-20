package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class SupplementsScrapResponseDTO {
    private Long supplementId;
    private boolean isScrapped;
    private int scrapCount;
    private String message;
}
package com.pharmquest.pharmquest.domain.supplements.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SupplementsScrapResponseDTO {
    private Long supplementId;
    private boolean isScrapped;
    private int scrapCount;
    private String message;
    private List<String> selectCategories;
}
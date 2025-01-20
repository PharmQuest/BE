package com.pharmquest.pharmquest.domain.pharmacy.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class PharmacyRequestDTO {

    @Getter
    public static class ScrapDto{
        @NotNull
        private Long userId;
        @NotBlank
        private String placeId;
    }

}

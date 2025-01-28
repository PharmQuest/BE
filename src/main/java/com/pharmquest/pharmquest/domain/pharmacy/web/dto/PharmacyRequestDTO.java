package com.pharmquest.pharmquest.domain.pharmacy.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class PharmacyRequestDTO {

    @Getter
    public static class FindDto {
        @NotNull
        @JsonProperty(value = "place_id")
        private String placeId;
    }

}

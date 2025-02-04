package com.pharmquest.pharmquest.domain.pharmacy.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PharmacyResponseDTO {

    @Builder
    @Getter
    public static class checkScrap{
        @JsonProperty("if_scrap")
        private Boolean ifScrap;

        public checkScrap(Boolean ifScrap) {
            this.ifScrap = ifScrap;
        }
    }

}
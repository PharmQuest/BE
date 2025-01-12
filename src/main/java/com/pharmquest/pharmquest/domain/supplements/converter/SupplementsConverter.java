package com.pharmquest.pharmquest.domain.supplements.converter;

import com.pharmquest.pharmquest.domain.supplements.domain.Supplements;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;

public class SupplementsConverter {
    public static SupplementsResponseDTO.SupplementsDto toSupplementsDto(Supplements supplements) {
        return SupplementsResponseDTO.SupplementsDto.builder()
                .name(supplements.getName())
                .image(supplements.getImage())
                .brand(supplements.getBrand())
                .build();
    }
}

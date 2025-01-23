package com.pharmquest.pharmquest.domain.supplements.service.Naver;

import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;

import java.util.List;

public interface NaverShoppingService {
    List<SupplementsResponseDTO.SupplementsInternalDto> loadProducts(String query);
}
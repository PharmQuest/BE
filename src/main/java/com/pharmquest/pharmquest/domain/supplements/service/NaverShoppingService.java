package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;

import java.util.List;

public interface NaverShoppingService {
    List<SupplementsResponseDTO.SupplementsInternalDto> loadProducts(String query);
}
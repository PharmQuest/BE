package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.enums.Nation;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface SupplementsService {
    Page<SupplementsResponseDTO.SupplementsDto> getSupplements(String category, Pageable pageable, Long userId);
    Page<SupplementsResponseDTO.SupplementsSearchResponseDto> searchSupplements(String keyword, Nation nation, Pageable pageable, Long userId);
    SupplementsResponseDTO.SupplementsDetailResponseDto getSupplementById(Long id, Long userId);
    boolean saveSupplements();
}
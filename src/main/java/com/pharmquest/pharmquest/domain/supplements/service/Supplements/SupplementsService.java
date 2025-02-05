package com.pharmquest.pharmquest.domain.supplements.service.Supplements;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryGroup;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import org.springframework.data.domain.Pageable;


public interface SupplementsService {
    SupplementsResponseDTO.SupplementsPageResponseDto getSupplements(CategoryGroup category, Pageable pageable, Long userId);
    SupplementsResponseDTO.SupplementsSearchPageResponseDto searchSupplements(String keyword, Country country, Pageable pageable, Long userId);
    SupplementsResponseDTO.SupplementsDetailResponseDto getSupplementById(Long id, Long userId);
    boolean saveSupplements();
}
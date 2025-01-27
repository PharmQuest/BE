package com.pharmquest.pharmquest.domain.supplements.service.Supplements;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface SupplementsService {
    Page<SupplementsResponseDTO.SupplementsDto> getSupplements(CategoryKeyword category, Pageable pageable, Long userId);
    Page<SupplementsResponseDTO.SupplementsSearchResponseDto> searchSupplements(String keyword, Country country, Pageable pageable, Long userId);
    SupplementsResponseDTO.SupplementsDetailResponseDto getSupplementById(Long id, Long userId);
    boolean saveSupplements();
}
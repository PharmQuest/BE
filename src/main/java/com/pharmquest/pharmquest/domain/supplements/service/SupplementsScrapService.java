package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsScrapResponseDTO;

public interface SupplementsScrapService {
    SupplementsScrapResponseDTO changeScrap(Long supplmenetsId, Long userId);
}

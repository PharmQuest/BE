package com.pharmquest.pharmquest.domain.supplements.service.Advertisement;

import com.pharmquest.pharmquest.domain.supplements.web.dto.AdResponseDTO;

public interface AdvertisementService {
    AdResponseDTO.AdResponseDto getAdvertisementByPage(int page);

    AdResponseDTO.AdLargeResponseDto getAdvertisementById(long id);
}

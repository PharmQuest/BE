package com.pharmquest.pharmquest.domain.supplements.service.Advertisement;

import com.pharmquest.pharmquest.domain.supplements.web.dto.AdResponseDTO;

import java.util.List;

public interface AdvertisementService {
    List<AdResponseDTO.AdResponseDto> getAdvertisementByPage(int page);
}

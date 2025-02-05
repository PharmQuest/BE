package com.pharmquest.pharmquest.domain.supplements.service.Advertisement;

import com.pharmquest.pharmquest.domain.supplements.data.Advertisement;
import com.pharmquest.pharmquest.domain.supplements.repository.AdvertisementRepository;
import com.pharmquest.pharmquest.domain.supplements.web.dto.AdResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private final AdvertisementRepository advertisementRepository;

    public AdResponseDTO.AdResponseDto getAdvertisementByPage(int page) {
        long totalAds = advertisementRepository.count();
        if (totalAds == 0) {
            throw new EntityNotFoundException("No advertisements available");
        }

        long adId = ((page - 1) % totalAds) + 1; // 모듈러 연산으로 광고 순환

        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));

        return AdResponseDTO.AdResponseDto.builder()
                .id(ad.getId())
                .smallImageUrl(ad.getSmallImageUrl())
                .build();
    }

    public AdResponseDTO.AdLargeResponseDto getAdvertisementById(long id) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        return AdResponseDTO.AdLargeResponseDto.builder()
                .id(ad.getId())
                .largeImageUrl(ad.getLargeImageUrl())
                .build();
    }
}

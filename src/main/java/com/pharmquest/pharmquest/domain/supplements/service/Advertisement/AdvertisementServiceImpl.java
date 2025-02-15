package com.pharmquest.pharmquest.domain.supplements.service.Advertisement;

import com.pharmquest.pharmquest.domain.supplements.data.Advertisement;
import com.pharmquest.pharmquest.domain.supplements.repository.AdvertisementRepository;
import com.pharmquest.pharmquest.domain.supplements.web.dto.AdResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private final AdvertisementRepository advertisementRepository;

    public AdResponseDTO.AdResponseDto getAdvertisementByPage(int page) {
        List<Advertisement> ads = advertisementRepository.findAll();
        if (ads.isEmpty()) {
            throw new EntityNotFoundException("No advertisements available");
        }

        int index = (page - 1) % ads.size();
        Advertisement ad = ads.get(index);

        return AdResponseDTO.AdResponseDto.builder()
                .id(ad.getId())
                .name(ad.getName())
                .image(ad.getImageUrl())
                .productName(ad.getName())
                .isAd(true)
                .build();
    }
}

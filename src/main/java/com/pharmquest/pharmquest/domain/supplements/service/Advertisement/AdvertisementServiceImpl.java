package com.pharmquest.pharmquest.domain.supplements.service.Advertisement;

import com.pharmquest.pharmquest.domain.supplements.data.Advertisement;
import com.pharmquest.pharmquest.domain.supplements.repository.AdvertisementRepository;
import com.pharmquest.pharmquest.domain.supplements.web.dto.AdResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private final AdvertisementRepository advertisementRepository;

    //페이지별 광고 반환(모듈러)
    public List<AdResponseDTO.AdResponseDto> getAdvertisementByPage(int page) {
        List<Advertisement> ads = advertisementRepository.findAll();
        if (ads.isEmpty()) {
            throw new EntityNotFoundException("No advertisements available");
        }
        List<AdResponseDTO.AdResponseDto> result = new ArrayList<>();

        // 첫 번째 광고
        int index1 = (page - 1) * 2 % ads.size();
        Advertisement ad1 = ads.get(index1);
        result.add(AdResponseDTO.AdResponseDto.builder()
                .id(ad1.getId())
                .name(ad1.getName())
                .image(ad1.getImageUrl())
                .productName(ad1.getName())
                .isAd(true)
                .build());

        // 두 번째 광고
        int index2 = ((page - 1) * 2 + 1) % ads.size();
        Advertisement ad2 = ads.get(index2);
        result.add(AdResponseDTO.AdResponseDto.builder()
                .id(ad2.getId())
                .name(ad2.getName())
                .image(ad2.getImageUrl())
                .productName(ad2.getName())
                .isAd(true)
                .build());

        return result;
    }
}

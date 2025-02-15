package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.pharmacy.ImageUtil;
import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;
import com.pharmquest.pharmquest.domain.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PharmacyS3UpdateService {

    private final PharmacyRepository pharmacyRepository;
    private final ImageUtil imageUtil;
    private final PharmacyDetailsService pharmacyDetailsService;

    @Scheduled(cron = "0 0 3 * * ?") // 3시마다 업데이트
    public void updatePharmacyImage() {

        log.info("약국 사진 업데이트 진행");
        List<Pharmacy> pharmacyList = pharmacyRepository.findAll();
        pharmacyList.stream()
                .filter(pharmacy -> !"data".equals(pharmacy.getImgUrl().substring(0, 4)))
                .forEach(pharmacy -> {
                    String placeId = pharmacy.getPlaceId();
                    String photoReference = pharmacyDetailsService.getPhotoReference(placeId);
                    pharmacy.setImgUrl(imageUtil.getPharmacyImageURL(photoReference));
                });
        pharmacyRepository.saveAll(pharmacyList);
        log.info("약국 사진 업데이트 종료");
    }


}

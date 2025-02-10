package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;
import com.pharmquest.pharmquest.domain.pharmacy.repository.PharmacyRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PharmacyCommandServiceImpl implements PharmacyCommandService {

    private final PharmacyDetailsService pharmacyDetailsService;
    private final PharmacyRepository pharmacyRepository;
    private final UserRepository userRepository;

    @Override
    public SuccessStatus scrapPharmacy(User user, String placeId) {

        List<String> pharmacyScraps = user.getPharmacyScraps();
        List<String> updatedPharmacyScraps = new ArrayList<>(pharmacyScraps);
        boolean isScraped = true;

        // 테이블에 약국 정보 이미 있는지 체크 후, 없다면 저장
        Boolean isExist = pharmacyRepository.existsByPlaceId(placeId);
        if (!isExist) {
            Pharmacy pharmacy = pharmacyDetailsService.getPharmacyByPlaceId(placeId);
            pharmacyRepository.save(pharmacy);
        }

        // 해당 약국이 이미 스크랩되어있는지 체크.
        if(!pharmacyScraps.contains(placeId)) { // 스크랩 되어있지 않았다면 저장

            // placeId 검증
            if(placeId == null || placeId.isEmpty()) { // 값이 없음
                throw new CommonExceptionHandler(ErrorStatus.PHARMACY_PLACE_ID_NULL);
            }else if(!pharmacyDetailsService.isPharmacyByPlaceId(placeId)) { // placeId에 해당하는 장소가 약국이 아님
                throw new CommonExceptionHandler(ErrorStatus.NOT_A_PHARMACY);
            }

            // 스크랩 목록에 placeId 추가
            try {
                updatedPharmacyScraps.add(placeId);
            } catch (DataIntegrityViolationException e) { // 저장 최대 수 초과 시
                throw new CommonExceptionHandler(ErrorStatus.PHARMACY_SCRAP_MAX_EXCEED);
            } catch (Exception e) { // 그 외 오류
                throw new CommonExceptionHandler(ErrorStatus.PHARMACY_UNKNOWN_ERROR);
            }

        }else{ // 스크랩 되어있다면 스크랩 취소
            updatedPharmacyScraps.remove(placeId);
            isScraped = false;
        }

        user.setPharmacyScraps(updatedPharmacyScraps);
        userRepository.save(user);
        return isScraped ? SuccessStatus.PHARMACY_SCRAP : SuccessStatus.PHARMACY_UNSCRAP;

    }
}

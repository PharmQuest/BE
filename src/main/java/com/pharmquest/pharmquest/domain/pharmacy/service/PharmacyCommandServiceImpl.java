package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
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

    @Override
    public Boolean scrapPharmacy(User user, String placeId) {

        List<String> pharmacyScraps = user.getPharmacyScraps();
        List<String> updatedPharmacyScraps = new ArrayList<>(pharmacyScraps);

        // 해당 약국이 이미 스크랩되어있는지 체크.
        // 스크랩되지 않았다면 저장.
        if(!pharmacyScraps.contains(placeId)) {

            // placeId 검증
            if(placeId == null || placeId.isEmpty()) { // 값이 잘못됨
                throw new CommonExceptionHandler(ErrorStatus.PHARMACY_BAD_PLACE_ID);
            }else if(!pharmacyDetailsService.isPharmacyByPlaceId(placeId)) { // placeId에 해당하는 장소가 약국이 아님
                throw new CommonExceptionHandler(ErrorStatus.NOT_A_PHARMACY);
            }

            // 스크랩 목록에 placeId 추가
            try {
                updatedPharmacyScraps.add(placeId);
                user.setPharmacyScraps(updatedPharmacyScraps);
            } catch (DataIntegrityViolationException e) { // 저장 최대 수 초과 시
                throw new CommonExceptionHandler(ErrorStatus.PHARMACY_SCRAP_MAX_EXCEED);
            }
            return true;
        // 스크랩 되어있다면 스크랩 취소
        }else{
            updatedPharmacyScraps.remove(placeId);
            user.setPharmacyScraps(updatedPharmacyScraps);
            return false;
        }
    }
}

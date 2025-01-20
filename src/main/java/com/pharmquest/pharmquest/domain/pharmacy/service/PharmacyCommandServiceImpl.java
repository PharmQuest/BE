package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PharmacyCommandServiceImpl implements PharmacyCommandService {

    private final UserRepository userRepository;

    @Override
    public Boolean scrapPharmacy(Long userId, String placeId) {

        System.out.println("service");
        User user = userRepository.findById(userId).orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        List<String> pharmacyScraps = user.getPharmacyScraps();

        // placeId 검증
        if(placeId == null || placeId.isEmpty()) {
            throw new CommonExceptionHandler(ErrorStatus.PHARMACY_BAD_PLACE_ID);
        }

        // 해당 약국이 이미 스크랩되어있는지 체크.
        // 스크랩되지 않았어야 저장.
        if (!pharmacyScraps.contains(placeId)) {
            List<String> updatedPharmacyScraps = new ArrayList<>(pharmacyScraps);
            updatedPharmacyScraps.add(placeId);
            user.setPharmacyScraps(updatedPharmacyScraps);
            return true;
        }
        return false;
    }
}

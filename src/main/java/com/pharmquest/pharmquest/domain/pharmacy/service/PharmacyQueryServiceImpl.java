package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PharmacyQueryServiceImpl implements PharmacyQueryService {

    @Override
    public Boolean checkIfScrapPharmacy(String placeId, User user) {
        List<String> pharmacyScraps = user.getPharmacyScraps();
        System.out.println("placeId = " + placeId);
        if (placeId == null) {
            throw new CommonExceptionHandler(ErrorStatus.PHARMACY_PLACE_ID_NULL);
        }

        String foundPlaceId = pharmacyScraps.stream()
                .filter(placeId::equals)
                .findFirst()
                .orElse(null);

        return foundPlaceId != null;
    }

}
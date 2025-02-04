package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.user.data.User;
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
            throw new IllegalArgumentException("placeId cannot be null");
        }

        String foundPlaceId = pharmacyScraps.stream()
                .filter(placeId::equals)
                .findFirst()
                .orElse(null);

        return foundPlaceId != null;
    }

}

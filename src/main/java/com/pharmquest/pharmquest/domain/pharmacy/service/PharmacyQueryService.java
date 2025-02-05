package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.user.data.User;

public interface PharmacyQueryService {
    public Boolean checkIfScrapPharmacy(String placeId, User user);
}

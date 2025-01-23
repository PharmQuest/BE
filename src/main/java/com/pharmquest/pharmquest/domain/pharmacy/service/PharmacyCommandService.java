package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.user.data.User;

public interface PharmacyCommandService {
    public Boolean scrapPharmacy(User user, String placeId);
}

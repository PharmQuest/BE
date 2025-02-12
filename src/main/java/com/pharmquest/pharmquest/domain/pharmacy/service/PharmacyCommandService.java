package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;

public interface PharmacyCommandService {
    public SuccessStatus scrapPharmacy(User user, String placeId);
    public Pharmacy savePharmacy(String placeId);
}

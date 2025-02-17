package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;
import com.pharmquest.pharmquest.domain.pharmacy.web.dto.GooglePlaceDetailsResponse;


public interface PharmacyDetailsService {
    public Pharmacy getPharmacyByPlaceId(String placeId);

    public String getPhotoReference(String placeId);
    public String getPhotoReference(GooglePlaceDetailsResponse response);
}

package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;


public interface PharmacyDetailsService {
    public Pharmacy getPharmacyByPlaceId(String placeId);

    public String getPhotoReference(String placeId);
}

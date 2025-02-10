package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;


public interface PharmacyDetailsService {
    public Boolean isPharmacyByPlaceId(String placeId);
    public MyPageResponseDTO.PharmacyDto getPharmacyDtoByPlaceId(String placeId);
    public Pharmacy getPharmacyByPlaceId(String placeId);
}

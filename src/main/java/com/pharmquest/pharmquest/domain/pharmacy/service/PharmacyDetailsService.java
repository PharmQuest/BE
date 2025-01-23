package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.ScrapResponseDTO;


public interface PharmacyDetailsService {
    public Boolean isPharmacyByPlaceId(String placeId);

    public ScrapResponseDTO.PharmacyDto getPharmacyByPlaceId(String placeId);
}

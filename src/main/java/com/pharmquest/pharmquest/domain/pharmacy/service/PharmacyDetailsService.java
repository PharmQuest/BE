package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;


public interface PharmacyDetailsService {
    public Boolean isPharmacyByPlaceId(String placeId);

    public MyPageResponseDTO.PharmacyDto getPharmacyDtoByPlaceId(String placeId);
}

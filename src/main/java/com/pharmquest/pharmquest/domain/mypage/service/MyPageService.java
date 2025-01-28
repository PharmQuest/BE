package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.user.data.User;

import java.util.List;

public interface MyPageService {

    List<Supplements> getScrapSupplements(Long userId);
    List<MyPageResponseDTO.PharmacyDto> getScrapPharmacies(User user, String country);


}

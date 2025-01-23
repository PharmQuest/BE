package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.ScrapResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;

import java.util.List;

public interface ScrapService {
    public List<ScrapResponseDTO.PharmacyDto> getPharmacies(User user, String country);
}

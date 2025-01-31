package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MyPageService {

    Page<MyPageResponseDTO.PharmacyDto> getScrapPharmacies(User user, String country, Integer page);
    Page<MyPageResponseDTO.SupplementsResponseDto> getScrapSupplements(Long userId, Pageable pageable, CategoryKeyword category);
}

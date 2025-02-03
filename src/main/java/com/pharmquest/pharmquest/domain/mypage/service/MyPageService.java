package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MyPageService {

    Page<MyPageResponseDTO.PharmacyDto> getScrapPharmacies(User user, PharmacyCountry country, Integer page, Integer size);
    Page<MyPageResponseDTO.SupplementsResponseDto> getScrapSupplements(Long userId, Pageable pageable, CategoryKeyword category);
    Page<MyPageResponseDTO.ScrapPostResponseDTO> getScrapPosts(Long userId, Pageable pageable);
    Page<MyPageResponseDTO.PostResponseDTO> getMyPosts(Long userId, Pageable pageable);
    Page<MyPageResponseDTO.CommentResponseDTO> getMyComments(Long userId, Pageable pageable);
    Page<MyPageResponseDTO.notificationResponseDTO> getNotification(Long userId, Pageable pageable);



}

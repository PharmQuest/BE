package com.pharmquest.pharmquest.domain.user.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.web.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDTO.UserResponseDto getUser(Long userId);
}

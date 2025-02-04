package com.pharmquest.pharmquest.domain.user.service;

import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.domain.user.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public UserDTO.UserResponseDto getUser(Long userId) {
        User user = userRepository.findNameById(userId);

        return UserDTO.UserResponseDto.builder()
                .userId(user.getId())
                .userName(user.getName())
                .build();
    }
}

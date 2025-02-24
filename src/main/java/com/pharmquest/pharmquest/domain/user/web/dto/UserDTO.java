package com.pharmquest.pharmquest.domain.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponseDto {
        private Long userId;
        private String userName;
        private String provider;
        private String email;
    }
}

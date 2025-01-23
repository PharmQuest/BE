package com.pharmquest.pharmquest.domain.home.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HomePostResponseDTO {

    private String createdAt;
    private String title;
    private String category;

}

package com.pharmquest.pharmquest.domain.home.dto;

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
    
    private LocalDateTime createdAt;
    private String title;
    private String category;

}

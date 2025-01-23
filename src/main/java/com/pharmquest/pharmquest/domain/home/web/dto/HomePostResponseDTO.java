package com.pharmquest.pharmquest.domain.home.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HomePostResponseDTO {

    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("created_at")
    private String createdAt;
    private String title;
    private String category;

}

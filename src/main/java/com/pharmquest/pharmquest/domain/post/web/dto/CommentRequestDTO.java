package com.pharmquest.pharmquest.domain.post.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class CommentRequestDTO {

    @Getter
    public static class CreateCommentDTO {

        @NotBlank
        String content;

    }

}

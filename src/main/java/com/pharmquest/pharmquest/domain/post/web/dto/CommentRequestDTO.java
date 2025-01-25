package com.pharmquest.pharmquest.domain.post.web.dto;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class CommentRequestDTO {

    @Getter
    public static class CreateCommentDTO {

        @NotBlank
        String content;

    }

}

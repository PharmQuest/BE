package com.pharmquest.pharmquest.domain.post.web.dto;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.user.data.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;

public class PostRequestDTO {

    @Getter
    public static class CreatePostDTO {

        @NotBlank
        String title;
        @NotBlank
        String content;
        @NotNull
        Country country;
        PostCategory category;


    }
}

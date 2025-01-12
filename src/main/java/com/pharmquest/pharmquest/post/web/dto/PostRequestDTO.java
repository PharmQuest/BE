package com.pharmquest.pharmquest.post.web.dto;

import com.pharmquest.pharmquest.post.domain.enums.Category;
import com.pharmquest.pharmquest.post.domain.enums.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PostRequestDTO {

    @Getter
    public static class CreatePostDTO {
        @NotBlank
        String title;
        @NotBlank
        String content;
        @NotNull
        Country country;
        Category category;


    }
}

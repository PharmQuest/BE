package com.pharmquest.pharmquest.domain.post.web.dto;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.user.data.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NonNull;

public class PostRequestDTO {

    @Getter
    public static class CreatePostDTO {

        @NotEmpty
        @Column(nullable = false, length = 100)
        String title;
        @NotEmpty
        @Column(nullable = false, length = 4000)
        String content;
        @NotNull
        Country country;
        PostCategory category;


    }

    @Getter
    public static class UpdatePostDTO {

        @Schema(description = "게시글 제목", nullable = true) // 기본값 생성을 막음
         String title;

        @Schema(description = "게시글 내용", nullable = true)
        String content;

        @Schema(description = "나라", nullable = true)
        Country country;

        @Schema(description = "카테고리", nullable = true)
        PostCategory category;

        Boolean deleteImgae;

    }

}

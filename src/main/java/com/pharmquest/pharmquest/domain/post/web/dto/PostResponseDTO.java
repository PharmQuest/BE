package com.pharmquest.pharmquest.domain.post.web.dto;

import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostResultDTO {
        Long postId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreViewListDTO {

        List<PostPreViewDTO> postList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreViewDTO {
        String user;
        String title;
        String content;
        String category;
        Integer scrapeCount;
        Integer likeCount;
        Integer commentCount;
        LocalDateTime createdAt;
        Boolean isBestPost;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetailDTO {
        String user;
        String title;
        String content;
        String category;
        Integer scrapeCount;
        Integer likeCount;
        Integer commentCount;
        LocalDateTime createdAt;
        Boolean isBestPost;
        Boolean isLiked;
        Boolean isScraped;
        Boolean isReported;
        private List<String> comments;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class CreatePostLikeResponseDTO{
        Long postLikeId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class CreatePostScrapResponseDTO{
        Long postLikeId;
        LocalDateTime createdAt;
    }

}


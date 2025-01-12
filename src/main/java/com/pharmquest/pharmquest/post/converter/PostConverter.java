package com.pharmquest.pharmquest.post.converter;

import com.pharmquest.pharmquest.post.domain.Post;
import com.pharmquest.pharmquest.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.post.web.dto.PostResponseDTO;

import java.time.LocalDateTime;

public class PostConverter {

    public static PostResponseDTO.CreatePostResultDTO toCreatePostResultDTO(Post post) {
        return PostResponseDTO.CreatePostResultDTO.builder()
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Post toPost(Long userId, PostRequestDTO.CreatePostDTO request) {

        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .country(request.getCountry())
                .build();
    }

}

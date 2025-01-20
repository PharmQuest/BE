package com.pharmquest.pharmquest.domain.post.converter;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;

import java.time.LocalDateTime;

public class PostLikeConverter {

    public static PostLike toPostLike(User user, Post post) {
        return PostLike.builder().user(user).post(post).build();
    }

    public static PostResponseDTO.CreatePostLikeResponseDTO toPostLikeDTO(PostLike postLike) {
        return PostResponseDTO.CreatePostLikeResponseDTO.builder()
                .postLikeId(postLike.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }


}

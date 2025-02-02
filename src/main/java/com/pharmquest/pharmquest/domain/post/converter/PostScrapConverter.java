package com.pharmquest.pharmquest.domain.post.converter;

import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import java.time.LocalDateTime;

public class PostScrapConverter {

    public static PostScrap toPostScrap(User user, Post post) {
        return PostScrap.builder().user(user).post(post).build();
    }

    public static PostResponseDTO.CreatePostScrapResponseDTO toPostScrapDTO(PostScrap postScrap) {
        return PostResponseDTO.CreatePostScrapResponseDTO.builder()
                .postLikeId(postScrap.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}

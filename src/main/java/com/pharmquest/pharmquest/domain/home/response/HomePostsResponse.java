package com.pharmquest.pharmquest.domain.home.response;

import com.pharmquest.pharmquest.domain.home.dto.HomePostResponseDTO;
import com.pharmquest.pharmquest.domain.post.domain.Post;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class HomePostsResponse {

    List<HomePostResponseDTO> hotPosts;
    List<HomePostResponseDTO> newPosts;

    public HomePostsResponse(List<Post> hotPosts, List<Post> newPosts) {
        this.hotPosts = hotPosts.stream()
                .map(post -> HomePostResponseDTO.builder()
                        .title(post.getTitle())
                        .createdAt(post.getCreatedAt())
                        .category(post.getCategory().getKoreanName())
                        .build()
                ).collect(Collectors.toList());

        this.newPosts = newPosts.stream()
                .map(post -> HomePostResponseDTO.builder()
                        .title(post.getTitle())
                        .createdAt(post.getCreatedAt())
                        .category(post.getCategory().getKoreanName())
                        .build()
                ).collect(Collectors.toList());
    }

}

package com.pharmquest.pharmquest.domain.home.response;

import com.pharmquest.pharmquest.domain.home.converter.HomePostConverter;
import com.pharmquest.pharmquest.domain.home.web.dto.HomePostResponseDTO;
import com.pharmquest.pharmquest.domain.post.data.Post;
import lombok.Getter;

import java.util.List;

@Getter
public class HomePostsResponse {

    private HomePostResponseDTO hotPost;
    private List<HomePostResponseDTO> newPosts;

    public HomePostsResponse(Post hotPost, List<Post> newPosts) {
        this.hotPost = HomePostConverter.postToDto(hotPost);
        this.newPosts = HomePostConverter.postListToDtoList(newPosts);
    }
}

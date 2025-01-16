package com.pharmquest.pharmquest.domain.post.service;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;


public interface PostCommandService {

    Post registerPost(Long userId, PostRequestDTO.CreatePostDTO request);
}

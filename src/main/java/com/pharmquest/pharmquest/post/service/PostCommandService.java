package com.pharmquest.pharmquest.post.service;

import com.pharmquest.pharmquest.post.domain.Post;
import com.pharmquest.pharmquest.post.web.dto.PostRequestDTO;

public interface PostCommandService {

    Post registerPost(Long userId, PostRequestDTO.CreatePostDTO request);
}

package com.pharmquest.pharmquest.domain.post.service;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import org.springframework.data.domain.Page;


public interface PostCommandService {

    Post registerPost(PostRequestDTO.CreatePostDTO request);

    Page<Post> getAllPosts(PostCategory category, Integer page);
}

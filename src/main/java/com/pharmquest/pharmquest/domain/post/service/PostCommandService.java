package com.pharmquest.pharmquest.domain.post.service;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;


import java.util.List;


public interface PostCommandService {

    Post registerPost(PostRequestDTO.CreatePostDTO request);

    Page<Post> getAllPosts(PostCategory category, Integer page);

    PostResponseDTO.PostDetailDTO getPost(Long postId);

    Page<Post> searchPostsDynamically(String keyword, Country country, PostCategory category, Integer page);
}

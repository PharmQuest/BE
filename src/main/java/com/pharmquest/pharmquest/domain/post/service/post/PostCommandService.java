package com.pharmquest.pharmquest.domain.post.service.post;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import org.springframework.data.domain.Page;


public interface PostCommandService {

    Post registerPost(Long userId, PostRequestDTO.CreatePostDTO request);

    Page<Post> getAllPosts(Long userId, PostCategory category, Integer page);

    PostResponseDTO.PostDetailDTO getPost(Long userId, Long postId, Integer page);

    Page<Post> searchPostsDynamically(Long userId, String keyword, Country country, PostCategory category, Integer page);

    void deletePost(Long userId, Long postId);

    Post updatePost(Long userId, Long postId, PostRequestDTO.UpdatePostDTO request);
}

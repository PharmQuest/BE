package com.pharmquest.pharmquest.domain.post.service;

import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.repository.PostRepository;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{

    private final PostRepository postRepository;

    @Override
    public Post registerPost(Long id, PostRequestDTO.CreatePostDTO request) {

        Post newPost = PostConverter.toPost(1L,request);

        return postRepository.save(newPost);
    }
}

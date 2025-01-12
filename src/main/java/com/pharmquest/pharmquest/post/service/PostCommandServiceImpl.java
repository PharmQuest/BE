package com.pharmquest.pharmquest.post.service;

import com.pharmquest.pharmquest.post.converter.PostConverter;
import com.pharmquest.pharmquest.post.domain.Post;
import com.pharmquest.pharmquest.post.repository.PostRepository;
import com.pharmquest.pharmquest.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.user.domain.User;
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

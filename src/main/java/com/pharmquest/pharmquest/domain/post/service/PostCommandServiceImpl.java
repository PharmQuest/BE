package com.pharmquest.pharmquest.domain.post.service;

import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.repository.PostRepository;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{

    private final PostRepository postRepository;

    @Override
    public Post registerPost(PostRequestDTO.CreatePostDTO request) {

        Post newPost = PostConverter.toPost(request);

        return postRepository.save(newPost);
    }

    @Override
    public Page<Post> getAllPosts(PostCategory category, Integer page) {
        if (category == PostCategory.ALL) {
            // category가 ALL이면 전체 게시물 조회
            return postRepository.findAll(PageRequest.of(page - 1, 10));
        } else {
            // 특정 category에 맞는 게시물 조회
            return postRepository.findByCategory(category, PageRequest.of(page - 1, 10));
        }
    }
}

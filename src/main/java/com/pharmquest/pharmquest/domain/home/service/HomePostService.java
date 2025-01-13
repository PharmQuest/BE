package com.pharmquest.pharmquest.domain.home.service;

import com.pharmquest.pharmquest.domain.home.repository.HomePostRepository;
import com.pharmquest.pharmquest.domain.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomePostService {

    private final HomePostRepository postRepository;

    public List<Post> getHotPosts(){

        // 핫 포스트 2개, 최신 포스트 3개
        List<Post> hotPosts = postRepository.findTop2ByOrderByScrapCountDesc();
        return hotPosts;
    }

    public List<Post> getNewPosts(){
        List<Post> newPosts = postRepository.findTop3ByOrderByCreatedAt();
        return newPosts;
    }


}

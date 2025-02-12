package com.pharmquest.pharmquest.domain.home.service;

import com.pharmquest.pharmquest.domain.home.repository.HomePostRepository;
import com.pharmquest.pharmquest.domain.post.data.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomePostService {

    private final HomePostRepository postRepository;

    // 인기 게시물 1개
    public Post getHotPost(){
        return postRepository.findHotPost().orElse(null);
    }

    // 최신 게시물 4개 반환
    // 최식 게시물 5개를 받아와 인기 게시물과 겹치는 하나는 제외
    public List<Post> getNewPosts(Post hotPost){

        List<Post> newPosts = postRepository.findTop5ByOrderByCreatedAtDesc();

        if(hotPost != null){ // 인기 게시물이 있을 경우 중복 제거
            newPosts.removeIf( post -> post.equals(hotPost));
        }
        int newPostsCount = hotPost == null ? 5 : 4; // 인기 게시글이 있으면 최신 게시글 5개, 없으면 4개로 설정
        if (newPosts.size() > newPostsCount) {
            newPosts.remove(newPosts.size() - 1);
        }

        return newPosts;

    }
}

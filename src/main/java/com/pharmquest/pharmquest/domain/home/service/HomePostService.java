package com.pharmquest.pharmquest.domain.home.service;

import com.pharmquest.pharmquest.domain.home.repository.HomePostRepository;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomePostService {

    private final HomePostRepository postRepository;

    public Post getHotPost(){
        // 인기 게시물 1개
        return postRepository.findHotPost().orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.POST_NOT_FOUND));
    }

    // 최신 게시물 4개 반환
    // 최식 게시물 5개를 받아와 인기 게시물과 겹치는 하나는 제외
    public List<Post> getNewPosts(Post hotPost){
        Long hotPostId = hotPost.getId();
        List<Post> newPosts = postRepository.findTop5ByOrderByCreatedAt();
        Post duplicatedPost = newPosts
                .stream()
                .filter(post -> post.getId().equals(hotPostId))
                .findFirst()
                .orElse(null);

        // 중복된 게시물 있으면 제거
        if(duplicatedPost != null){
            newPosts.remove(duplicatedPost);
        }else{ // 중복된 게시물 없으면 마지막 요소 제거
            newPosts.remove(newPosts.size()-1);
        }

        return newPosts;
    }


}

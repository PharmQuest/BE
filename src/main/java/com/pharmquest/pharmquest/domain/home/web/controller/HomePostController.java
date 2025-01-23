package com.pharmquest.pharmquest.domain.home.web.controller;

import com.pharmquest.pharmquest.domain.home.response.HomePostsResponse;
import com.pharmquest.pharmquest.domain.home.service.HomePostService;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomePostController {

    private final HomePostService homePostService;

    @GetMapping("/posts")
    public ApiResponse<HomePostsResponse> getHomePosts() {

        Post hotPost = homePostService.getHotPosts();
        List<Post> newPosts = homePostService.getNewPosts(hotPost);
        // 두 개의 List로 응답 생성
        return ApiResponse.of(SuccessStatus.HOME_POSTS, new HomePostsResponse(hotPost, newPosts));
    }

}
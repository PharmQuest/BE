package com.pharmquest.pharmquest.domain.home.web.controller;

import com.pharmquest.pharmquest.domain.home.response.HomePostsResponse;
import com.pharmquest.pharmquest.domain.home.service.HomePostService;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "메인화면")
@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomePostController {

    private final HomePostService homePostService;

    @GetMapping("/posts")
    @Operation(summary = "메인화면 게시글 조회 API", description = "메인화면에서 인기 게시글 1개, 최신 게시글 4개를 불러옵니다.\n" +
            "만약 아무도 게시글을 스크랩하지 않았다면 최신 게시글이 5개 조회됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HOME201", description = "홈 게시글을 성공적으로 불러왔습니다.")
    })
    public ApiResponse<HomePostsResponse> getHomePosts() {

        Post hotPost = homePostService.getHotPost();
        List<Post> newPosts = homePostService.getNewPosts(hotPost);
        // 두 개의 List로 응답 생성
        return ApiResponse.of(SuccessStatus.HOME_POSTS, new HomePostsResponse(hotPost, newPosts));
    }
}
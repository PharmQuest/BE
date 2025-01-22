package com.pharmquest.pharmquest.domain.post.web.controller;

import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.service.PostCommandService;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostController {

    private final PostCommandService postCommandService;
    private final JwtUtil jwtUtil;
    @PostMapping("/posts")
    public ApiResponse<PostResponseDTO.CreatePostResultDTO> postCommandService(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                                               @RequestBody @Valid PostRequestDTO.CreatePostDTO request){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        Post post = postCommandService.registerPost(user.getId(), request);
        return ApiResponse.onSuccess(PostConverter.toCreatePostResultDTO(post));
    }

    @GetMapping("/posts/lists")
    @Operation(summary = "카테고리별 게시글 리스트 조회 API")
    public ApiResponse<PostResponseDTO.PostPreViewListDTO> getPostList(@RequestParam(name = "category")PostCategory category, @RequestParam(name="page")Integer page){
        Page<Post> postList = postCommandService.getAllPosts(category, page);
        return ApiResponse.onSuccess(PostConverter.postPreViewListDTO(postList));
    }

    @GetMapping("/posts/{post_id}")
    @Operation(summary = "게시글 상세조회 API")
    public ApiResponse<PostResponseDTO.PostDetailDTO> getPost(@PathVariable(name = "post_id")Long postId){
        // 서비스 호출
        PostResponseDTO.PostDetailDTO postDetail = postCommandService.getPost(postId);

        return ApiResponse.onSuccess(postDetail);
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 키워드로 검색 API")
    public ApiResponse<PostResponseDTO.PostPreViewListDTO> searchPost(@RequestParam(name = "keyword")String keyword,@RequestParam(name = "country") Country country, @RequestParam(name = "category")PostCategory category, @RequestParam(name="page")Integer page){
        Page<Post> postList = postCommandService.searchPostsDynamically(keyword, country, category, page);
        return ApiResponse.onSuccess(PostConverter.postPreViewListDTO(postList));
    }

}

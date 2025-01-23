package com.pharmquest.pharmquest.domain.post.web.controller;

import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.post.converter.PostLikeConverter;
import com.pharmquest.pharmquest.domain.post.converter.PostScrapConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.post.service.PostCommandService;
import com.pharmquest.pharmquest.domain.post.service.like.PostLikeService;
import com.pharmquest.pharmquest.domain.post.service.scrap.PostScrapService;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostController {

    private final PostCommandService postCommandService;
    private final PostLikeService postLikeService;
    private final PostScrapService postScrapService;
    private final JwtUtil jwtUtil;

    @PostMapping("/posts")
    public ApiResponse<PostResponseDTO.CreatePostResultDTO> postCommandService(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid PostRequestDTO.CreatePostDTO request) {

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
    public ApiResponse<PostResponseDTO.PostDetailDTO> getPost(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,@PathVariable(name = "post_id")Long postId){
     
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        PostResponseDTO.PostDetailDTO postDetail = postCommandService.getPost(user.getId(), postId);

        return ApiResponse.onSuccess(postDetail);
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 키워드로 검색 API")
    public ApiResponse<PostResponseDTO.PostPreViewListDTO> searchPost(@RequestParam(name = "keyword")String keyword,@RequestParam(name = "country") Country country, @RequestParam(name = "category")PostCategory category, @RequestParam(name="page")Integer page){
        Page<Post> postList = postCommandService.searchPostsDynamically(keyword, country, category, page);
        return ApiResponse.onSuccess(PostConverter.postPreViewListDTO(postList));
    }

    @PostMapping("/posts/{post_id}/likes")
    @Operation(summary = "게시글 좋아요 API")
    public ApiResponse<PostResponseDTO.CreatePostLikeResponseDTO> createPostLike(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "post_id")Long postId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        PostLike postLike = postLikeService.createPostLike(user.getId(), postId);
        return ApiResponse.onSuccess(PostLikeConverter.toPostLikeDTO(postLike));
    }

    @DeleteMapping("/posts/{post_id}/likes")
    @Operation(summary = "게시글 좋아요 취소 API")
    public ApiResponse<String> deletePostLike(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "post_id")Long postId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        postLikeService.deletePostLike(user.getId(), postId);
        return ApiResponse.onSuccess("좋아요가 삭제되었습니다");
    }

    @PostMapping("/posts/{post_id}/scraps")
    @Operation(summary = "게시글 스크랩 API")
    public ApiResponse<PostResponseDTO.CreatePostScrapResponseDTO> createPostScrap(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "post_id")Long postId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        PostScrap postScrap = postScrapService.createPostScrap(user.getId(), postId);
        return ApiResponse.onSuccess(PostScrapConverter.toPostScrapDTO(postScrap));
    }

    @DeleteMapping("/posts/{post_id}/scraps")
    @Operation(summary = "게시글 스크랩 취소 API")
    public ApiResponse<String> deletePostScraps(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "post_id")Long postId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        postScrapService.deletePostScrap(user.getId(), postId);
        return ApiResponse.onSuccess("스크랩이 취소되었습니다");
    }


}

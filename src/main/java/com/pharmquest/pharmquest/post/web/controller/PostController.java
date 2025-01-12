package com.pharmquest.pharmquest.post.web.controller;

import com.pharmquest.pharmquest.post.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.post.converter.PostConverter;
import com.pharmquest.pharmquest.post.domain.Post;
import com.pharmquest.pharmquest.post.service.PostCommandService;
import com.pharmquest.pharmquest.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.post.web.dto.PostResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostController {

    private final PostCommandService postCommandService;

    @PostMapping("/posts")
    public ApiResponse<PostResponseDTO.CreatePostResultDTO> postCommandService(@RequestBody @Valid PostRequestDTO.CreatePostDTO request){

        Long userId =3L;

        Post post = postCommandService.registerPost(userId, request);
        return ApiResponse.onSuccess(PostConverter.toCreatePostResultDTO(post));
    }


}

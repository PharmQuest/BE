package com.pharmquest.pharmquest.domain.post.web.controller;

import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.service.PostCommandService;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
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

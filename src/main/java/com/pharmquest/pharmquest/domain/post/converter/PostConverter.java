package com.pharmquest.pharmquest.domain.post.converter;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentResponseDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {

    public static PostResponseDTO.CreatePostResultDTO toCreatePostResultDTO(Post post) {
        return PostResponseDTO.CreatePostResultDTO.builder()
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Post toPost(PostRequestDTO.CreatePostDTO request) {

        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .country(request.getCountry())
                .build();
    }

    public static PostResponseDTO.PostPreViewDTO postPreViewDTO(Post post) {
        return PostResponseDTO.PostPreViewDTO.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .userName(post.getUser().getEmail().substring(0, post.getUser().getEmail().indexOf("@")))
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory().getKoreanName())
                .scrapeCount(post.getScraps().size())
                .likeCount(post.getLikes().size())
                .commentCount(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static PostResponseDTO.PostPreViewListDTO postPreViewListDTO(Page<Post> postList){
        List<PostResponseDTO.PostPreViewDTO> postPreViewDTOList = postList.stream().map(PostConverter::postPreViewDTO).collect(Collectors.toList());

        return PostResponseDTO.PostPreViewListDTO.builder()
                .isFirst(postList.isFirst())
                .isLast(postList.isLast())
                .totalPage(postList.getTotalPages())
                .totalElements(postList.getTotalElements())
                .listSize(postPreViewDTOList.size())
                .postList(postPreViewDTOList)
                .build();
    }

    public static PostResponseDTO.PostDetailDTO postDetailDTO(
            Post post,
            Boolean isLiked,
            Boolean isScraped,
            Boolean isReported,
            List<CommentResponseDTO.CommentDTO> topLevelComment,
            Page<Comment> parentCommentsPage
    ) {
        return PostResponseDTO.PostDetailDTO.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .userName(post.getUser().getEmail().substring(0, post.getUser().getEmail().indexOf("@")))
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory().getKoreanName())
                .scrapeCount(post.getScraps().size())
                .likeCount(post.getLikes().size())
                .commentCount(post.getComments().size())
                .isLiked(isLiked)
                .isScraped(isScraped)
                .isReported(isReported)
                .comments(topLevelComment)
                .isFirst(parentCommentsPage.isFirst())
                .isLast(parentCommentsPage.isLast())
                .totalPage(parentCommentsPage.getTotalPages())
                .totalElements(parentCommentsPage.getTotalElements())
                .listSize(parentCommentsPage.getSize())
                .createdAt(post.getCreatedAt())
                .build();
    }

}


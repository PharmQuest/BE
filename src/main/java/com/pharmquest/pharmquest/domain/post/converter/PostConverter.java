package com.pharmquest.pharmquest.domain.post.converter;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentResponseDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostConverter {

    public static PostResponseDTO.postResultDTO toPostResultDTO(Post post) {
        return PostResponseDTO.postResultDTO.builder()
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Post toPost(PostRequestDTO.CreatePostDTO request, String imageUrl) {
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .country(request.getCountry())
                .postImgURL(imageUrl)
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
                .isBestPost(post.isBestPost())
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
            Boolean isOwnPost,
            Boolean isBestPost,
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
                .country(post.getCountry().getKoreanName())
                .scrapeCount(post.getScraps().size())
                .likeCount(post.getLikes().size())
                .commentCount(post.getComments().size())
                .isLiked(isLiked)
                .isScraped(isScraped)
                .isOwnPost(isOwnPost)
                .isBestPost(isBestPost)
                .isReported(isReported)
                .imageUrl(post.getPostImgURL())
                .comments(topLevelComment)
                .isFirst(parentCommentsPage.isFirst())
                .isLast(parentCommentsPage.isLast())
                .totalPage(parentCommentsPage.getTotalPages())
                .totalElements(parentCommentsPage.getTotalElements())
                .listSize(parentCommentsPage.getSize())
                .createdAt(post.getCreatedAt())
                .build();
    }


    public static PostResponseDTO.BestPostListDTO postRandomDTO(List<Post> postList) {
        List<PostResponseDTO.PostPreViewDTO> postPreViewDTOList = postList.stream()
                .map(PostConverter::postPreViewDTO)
                .collect(Collectors.toList());

        return PostResponseDTO.BestPostListDTO.builder()
                .listSize(postPreViewDTOList.size())
                .postList(postPreViewDTOList)
                .build();
    }

}


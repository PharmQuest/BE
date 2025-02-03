package com.pharmquest.pharmquest.domain.mypage.converter;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MyPageConverter {
    private final SupplementsCategoryRepository supplementsCategoryRepository;

    public MyPageResponseDTO.SupplementsResponseDto toSupplementsDto(Supplements supplements, CategoryKeyword category) {
        List<String> categories = supplementsCategoryRepository.findCategoryNamesBySupplementId(supplements.getId());

        if (category == null || category == CategoryKeyword.전체) {
            return MyPageResponseDTO.SupplementsResponseDto.builder()
                    .id(supplements.getId())
                    .name(supplements.getName())
                    .image(supplements.getImage())
                    .categories(categories)
                    .build();
        } else {
            boolean isCategoryMatched = categories.stream()
                    .anyMatch(c -> c.equals(category.toString()));

            if (isCategoryMatched) {
                return MyPageResponseDTO.SupplementsResponseDto.builder()
                        .id(supplements.getId())
                        .name(supplements.getName())
                        .image(supplements.getImage())
                        .categories(categories)
                        .build();
            } else {
                return null;
            }
        }
    }

    public MyPageResponseDTO.ScrapPostResponseDTO toScrapedPostDto(Post post) {
        return MyPageResponseDTO.ScrapPostResponseDTO.builder()
                .postId(post.getId())
                .writerName(post.getUser().getName())
                .title(post.getTitle())
                .category(post.getCategory())
                .content(post.getContent().length() <= 40 ? post.getContent() : post.getContent().substring(0, 40))
                .commentCount(post.getComments().size())
                .scrapeCount(post.getScraps().size())
                .likeCount(post.getLikes().size())
                .createdAt(post.getCreatedAt().toLocalDate())
                .build();
    }

    public MyPageResponseDTO.PostResponseDTO toPostDto(Post post) {
        return MyPageResponseDTO.PostResponseDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .content(post.getContent().length() <= 40 ? post.getContent() : post.getContent().substring(0, 40))
                .commentCount(post.getComments().size())
                .scrapeCount(post.getScraps().size())
                .likeCount(post.getLikes().size())
                .createdAt(post.getCreatedAt().toLocalDate())
                .build();
    }

    public static MyPageResponseDTO.PharmacyResponse toPharmaciesResponse(Page<MyPageResponseDTO.PharmacyDto> pharmacies) {

        List<MyPageResponseDTO.PharmacyDto> list = pharmacies.stream().toList();

        return MyPageResponseDTO.PharmacyResponse.builder()
                .pharmacies(list)
                .count((int) pharmacies.getTotalElements())
                .build();
    }

    public MyPageResponseDTO.CommentResponseDTO toCommentDto(Comment comment) {
        return MyPageResponseDTO.CommentResponseDTO.builder()
                .commentId(comment.getId())
                .title(comment.getPost().getTitle())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toLocalDate())
                .build();
    }

    public MyPageResponseDTO.notificationResponseDTO tonotificationCommentDTO(Comment comment) {
        return MyPageResponseDTO.notificationResponseDTO.builder()
                .postId(comment.getPost().getId())
                .commentContent(comment.getContent())
                .commentWriter(comment.getUser().getName())
                .postTitle(comment.getPost().getTitle())
                .createdAt(comment.getCreatedAt().toLocalDate())
                .build();
    }
}

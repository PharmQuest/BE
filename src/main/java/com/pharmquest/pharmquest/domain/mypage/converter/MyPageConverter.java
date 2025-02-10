package com.pharmquest.pharmquest.domain.mypage.converter;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MyPageConverter {
    private final SupplementsCategoryRepository supplementsCategoryRepository;
    private final SupplementsScrapRepository supplementsScrapRepository;

    private String processProductName(String name) {
        return name.replaceAll("^\\[(한국|미국|일본)\\]\\s*", "");
    }

    private String processCountryName(Country country) {
        if (country == Country.KOREA) {
            return "한국";
        }
        else if (country == Country.USA) {
            return "미국";
        }
        else if (country == Country.JAPAN) {
            return "일본";
        }
        return "";
    }

    public MyPageResponseDTO.SupplementsResponseDto toSupplementsDto(Supplements supplements) {
        List<String> categories = supplementsCategoryRepository.findCategoryNamesBySupplementId(supplements.getId());
        return MyPageResponseDTO.SupplementsResponseDto.builder()
                .id(supplements.getId())
                .name(supplements.getName())
                .image(supplements.getImage())
                .brand(supplements.getBrand())
                .productName(processProductName(supplements.getName()))
                .country(processCountryName(supplements.getCountry()))
                .isScrapped(true)
                .categories(categories)
                .build();
    }


    public MyPageResponseDTO.ScrapPostResponseDTO toScrapedPostDto(Post post) {
        return MyPageResponseDTO.ScrapPostResponseDTO.builder()
                .postId(post.getId())
                .writerName(post.getUser().getEmail().substring(0, post.getUser().getEmail().indexOf("@")))
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

    public MyPageResponseDTO.MedicineResponseDto toMedicineDto(Medicine medicine) {
        return MyPageResponseDTO.MedicineResponseDto.builder()
                .id(medicine.getId())
                .productName(medicine.getBrandName())
                .generalName(medicine.getSubstanceName())
                .productImage(medicine.getImgUrl())
                .country(medicine.getCountry())
                .categories(medicine.getCategory().name())
                .build();
    }

    public static MyPageResponseDTO.PharmacyResponse toPharmaciesResponse(Page<MyPageResponseDTO.PharmacyDto> pharmacies, int page, int size) {

        List<MyPageResponseDTO.PharmacyDto> list = pharmacies.stream().toList();

        return MyPageResponseDTO.PharmacyResponse.builder()
                .pharmacies(list)
                .totalElements((int) pharmacies.getTotalElements())
                .totalPages(pharmacies.getTotalPages())
                .currentPage(page)
                .elementsPerPage(size)
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

    public MyPageResponseDTO.notificationResponseDTO toNotificationCommentDTO(Comment comment) {
        return MyPageResponseDTO.notificationResponseDTO.builder()
                .postId(comment.getPost().getId())
                .commentContent(comment.getContent())
                .commentWriter(comment.getUser().getEmail().substring(0, comment.getUser().getEmail().indexOf("@")))
                .postTitle(comment.getPost().getTitle())
                .createdAt(comment.getCreatedAt().toLocalDate())
                .build();
    }
}

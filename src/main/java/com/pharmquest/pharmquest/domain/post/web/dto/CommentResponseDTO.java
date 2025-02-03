package com.pharmquest.pharmquest.domain.post.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class commentResultDTO {
        Long commentId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDTO {
        private Long commentId;
        private String content;
        private Long userId;
        private String userName;
        private LocalDateTime createdAt;
        private Long parentId;
        private String parentName;
        private Boolean isOwnPost;
        private Integer likeCount;
        private Boolean isLiked;
        private List<CommentDTO> replies;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class CreateCommentLikeResponseDTO{
        Long commentLikeId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class CreateCommentReportResponseDTO{
        Long commentReportId;
        LocalDateTime createdAt;
    }

}

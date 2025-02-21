package com.pharmquest.pharmquest.domain.post.converter;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.ReportType;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.data.mapping.CommentLike;
import com.pharmquest.pharmquest.domain.post.data.mapping.CommentReport;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostReport;
import com.pharmquest.pharmquest.domain.post.repository.comment.CommentLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.comment.PostCommentRepository;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentResponseDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostCommentConverter {


    @Builder
    public static CommentResponseDTO.CommentDTO toComment(Comment comment,Boolean isLiked, Boolean isPostAuthor, Boolean isOwnComment, Boolean isReported) {

        return CommentResponseDTO.CommentDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getEmail().substring(0, comment.getUser().getEmail().indexOf("@")))
                .likeCount(comment.getLikes().size())
                .isLiked(isLiked)
                .isPostAuthor(isPostAuthor)
                .isOwnComment(isOwnComment)
                .isDeleted(comment.isDeleted())
                .isReported(isReported)
                .createdAt(comment.getCreatedAt())
                .parentId(comment.getParent() != null ? comment.getParent().getId():null)
                .parentName(comment.getParent() != null ? comment.getParent().getUser().getEmail().substring(0, comment.getParent().getUser().getEmail().indexOf("@")): null)
                .replies(Collections.emptyList())  // 자식 댓글들
                .build();
    }


    public static Comment toAddComment(CommentRequestDTO.CreateCommentDTO request) {

        return Comment.builder()
                .content(request.getContent())
                .build();
    }


    public static CommentResponseDTO.commentResultDTO toCommentResultDTO(Comment comment) {
        return CommentResponseDTO.commentResultDTO.builder()
                .commentId(comment.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static CommentLike toCommentLike(User user, Comment comment) {
        return CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();
    }

    public static CommentResponseDTO.CreateCommentLikeResponseDTO toCommentLikeDTO(CommentLike commentLike) {
        return CommentResponseDTO.CreateCommentLikeResponseDTO.builder()
                .commentLikeId(commentLike.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static CommentReport toCommentReport(User user, Comment comment, ReportType reportType) {
        return CommentReport.builder()
                .user(user)
                .comment(comment)
                .type(reportType)
                .build();
    }

    public static CommentResponseDTO.CreateCommentReportResponseDTO toCommentReportDTO(CommentReport commentReport) {
        return CommentResponseDTO.CreateCommentReportResponseDTO.builder()
                .commentReportId(commentReport.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
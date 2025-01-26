package com.pharmquest.pharmquest.domain.post.converter;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentResponseDTO;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostCommentConverter {

    public static CommentResponseDTO.CommentDTO toComment(Comment comment) {
        // 댓글의 자식 댓글 불러오기
        List<CommentResponseDTO.CommentDTO> replies = Optional.ofNullable(comment.getChildren())
                .orElse(Collections.emptyList())  // null인 경우 빈 리스트로 처리
                .stream()
                .map(PostCommentConverter::toComment)
                .collect(Collectors.toList());

        return CommentResponseDTO.CommentDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getEmail().substring(0, comment.getUser().getEmail().indexOf("@")))
                .createdAt(comment.getCreatedAt())
                .parentId(comment.getParent() != null ? comment.getParent().getId():null)
                .parentName(comment.getParent() != null ? comment.getParent().getUser().getEmail().substring(0, comment.getParent().getUser().getEmail().indexOf("@")): null)
                .replies(replies)  // 자식 댓글들
                .build();
    }

    public static Comment toAddComment(CommentRequestDTO.CreateCommentDTO request) {

        return Comment.builder()
                .content(request.getContent())
                .build();
    }


    public static CommentResponseDTO.CreateCommentResultDTO toCreateCommentResultDTO(Comment comment) {
        return CommentResponseDTO.CreateCommentResultDTO.builder()
                .commentId(comment.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}

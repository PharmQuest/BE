package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentRequestDTO;

import java.util.List;

public interface PostCommentService {

    Comment addComment(Long userId, Long postId, Long parentId, CommentRequestDTO.CreateCommentDTO request);

    Comment updateComment(Long userId, Long commentId, CommentRequestDTO.UpdateCommentDTO request);

    void deleteComment(Long userId, List<Long> commentIds);
}

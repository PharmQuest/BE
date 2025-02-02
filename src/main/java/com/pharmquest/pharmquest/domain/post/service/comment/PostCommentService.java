package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentRequestDTO;

public interface PostCommentService {

    Comment addComment(Long userId, Long postId, Long parentId, CommentRequestDTO.CreateCommentDTO request);

    Comment updateComment(Long userId, Long commentId, CommentRequestDTO.UpdateCommentDTO request);

    Comment deleteComment(Long userId, Long commentId);
}

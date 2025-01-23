package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentResponseDTO;

public interface PostCommentService {

    CommentResponseDTO.CommentListDTO getCommentsByPost(Long postId);


    Comment addComment(Long userId, Long postId, Long parentId, CommentRequestDTO.CreateCommentDTO request);

}

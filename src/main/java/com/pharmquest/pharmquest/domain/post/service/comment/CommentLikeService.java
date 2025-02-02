package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.data.mapping.CommentLike;


public interface CommentLikeService {

    CommentLike createCommentLike(
            Long userId, Long commentId);

    void deleteCommentLike(
            Long userId, Long commentId);
}

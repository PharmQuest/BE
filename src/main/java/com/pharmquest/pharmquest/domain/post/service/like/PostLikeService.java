package com.pharmquest.pharmquest.domain.post.service.like;

import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;

public interface PostLikeService {

    PostLike createPostLike(
            Long userId, Long postId);

    void deletePostLike(
          Long userId, Long postId);
}

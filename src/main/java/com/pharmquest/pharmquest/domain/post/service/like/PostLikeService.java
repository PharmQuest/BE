package com.pharmquest.pharmquest.domain.post.service.like;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.user.data.User;

public interface PostLikeService {

    PostLike createPostLike(
            Long userId, Long postId);

    void deletePostLike(
          Long userId, Long postId);
}

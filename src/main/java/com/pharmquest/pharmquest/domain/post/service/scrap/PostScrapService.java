package com.pharmquest.pharmquest.domain.post.service.scrap;

import com.pharmquest.pharmquest.domain.mypage.domain.PostScrap;


public interface PostScrapService {

    PostScrap createPostScrap(
            Long userId, Long postId);

    void deletePostScrap(
            Long userId, Long postId);
}

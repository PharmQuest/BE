package com.pharmquest.pharmquest.domain.post.service.scrap;

import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;


public interface PostScrapService {

    PostScrap createPostScrap(
            Long userId, Long postId);

    void deletePostScrap(
            Long userId, Long postId);
}

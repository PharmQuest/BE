package com.pharmquest.pharmquest.domain.post.repository;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;

public interface PostRepositoryCustom {

    void createPost(Long userId, String title, String content, Country country, PostCategory category);

}

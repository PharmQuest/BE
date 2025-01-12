package com.pharmquest.pharmquest.post.repository;

import com.pharmquest.pharmquest.post.domain.enums.Category;
import com.pharmquest.pharmquest.post.domain.enums.Country;

public interface PostRepositoryCustom {

    void createPost(Long userId, String title, String content, Country country, Category category);

}

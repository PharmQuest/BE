package com.pharmquest.pharmquest.domain.post.service.bestPost;

import com.pharmquest.pharmquest.domain.post.data.Post;
import org.springframework.data.domain.Page;

public interface BestPostService {

    Page<Post> getBestPosts(Long userId, Integer page);
}

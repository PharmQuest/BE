package com.pharmquest.pharmquest.domain.post.service.bestPost;

import com.pharmquest.pharmquest.domain.post.data.BestPost;
import com.pharmquest.pharmquest.domain.post.data.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BestPostService {

    Page<Post> getBestPosts(Long userId, Integer page);

    List<Post> getRandomBestPosts(Long userId, int count);
}

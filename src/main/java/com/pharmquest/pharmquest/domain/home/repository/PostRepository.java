package com.pharmquest.pharmquest.domain.home.repository;

import com.pharmquest.pharmquest.domain.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findTop2ByOrderByScrapCountDesc();
    List<Post> findTop3ByOrderByCreatedAt();
}

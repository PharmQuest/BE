package com.pharmquest.pharmquest.domain.home.repository;

import com.pharmquest.pharmquest.domain.post.data.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HomePostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findTop1ByOrderByScrapCountDesc();
    List<Post> findTop5ByOrderByCreatedAt();
}

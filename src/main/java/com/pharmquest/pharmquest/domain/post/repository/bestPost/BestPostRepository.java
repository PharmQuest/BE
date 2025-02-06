package com.pharmquest.pharmquest.domain.post.repository.bestPost;

import com.pharmquest.pharmquest.domain.post.data.BestPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestPostRepository extends JpaRepository<BestPost, Long> {
    boolean existsByPostId(Long postId);

}

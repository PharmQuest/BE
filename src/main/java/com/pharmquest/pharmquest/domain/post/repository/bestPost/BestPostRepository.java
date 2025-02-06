package com.pharmquest.pharmquest.domain.post.repository.bestPost;

import com.pharmquest.pharmquest.domain.post.data.BestPost;
import com.pharmquest.pharmquest.domain.post.data.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BestPostRepository extends JpaRepository<BestPost, Long> {
    boolean existsByPostId(Long postId);

    @Query("SELECT b.post FROM BestPost b")
    Page<Post> findBestPosts(Pageable pageable);

}

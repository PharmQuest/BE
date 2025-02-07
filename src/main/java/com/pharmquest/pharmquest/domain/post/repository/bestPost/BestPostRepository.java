package com.pharmquest.pharmquest.domain.post.repository.bestPost;

import com.pharmquest.pharmquest.domain.post.data.BestPost;
import com.pharmquest.pharmquest.domain.post.data.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BestPostRepository extends JpaRepository<BestPost, Long> {
    boolean existsByPostId(Long postId);

    @Query("SELECT b.post FROM BestPost b")
    Page<Post> findBestPosts(Pageable pageable);

    @Query(value = "SELECT p.id FROM best_post b JOIN post p ON b.post_id = p.id ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Long> findRandomPostIds(@Param("count") int count);


    BestPost findByPostId(Long postId);
}

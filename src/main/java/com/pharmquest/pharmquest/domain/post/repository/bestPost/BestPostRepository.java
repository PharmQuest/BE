package com.pharmquest.pharmquest.domain.post.repository.bestPost;

import com.pharmquest.pharmquest.domain.post.data.BestPost;
import com.pharmquest.pharmquest.domain.post.data.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BestPostRepository extends JpaRepository<BestPost, Long> {
    boolean existsByPostId(Long postId);

    @Query("SELECT b.post FROM BestPost b")
    Page<Post> findBestPosts(Pageable pageable);

    @Query("SELECT distinct b.post FROM BestPost b " +
            "JOIN FETCH b.post.user " +
            "WHERE b.post.isDeleted = false AND b.post.id NOT IN " +
            "(SELECT pr.post.id FROM PostReport pr WHERE pr.user.id = :userId)")
    Page<Post> findBestPostsByUser(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT p.id FROM best_post b JOIN post p ON b.post_id = p.id ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Long> findRandomPostIds(@Param("count") int count);


    @Query(value = "SELECT p.id FROM best_post b " +
            "JOIN post p ON b.post_id = p.id " +
            "WHERE p.is_deleted = false " +
            "AND NOT EXISTS ( " +
            "    SELECT 1 FROM post_report pr WHERE pr.post_id = p.id AND pr.user_id = :userId " +
            ") " +
            "ORDER BY RAND() " +
            "LIMIT :count", nativeQuery = true)
    List<Long> findRandomPostIdsExcludingReportedByUser(@Param("userId") Long userId, @Param("count") int count);

    BestPost findByPostId(Long postId);
}

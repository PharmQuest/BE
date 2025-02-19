package com.pharmquest.pharmquest.domain.post.repository.post;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom, JpaSpecificationExecutor<Post> {

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.user " +
            " WHERE p.category = :category AND p.isDeleted = false " +
             "AND NOT EXISTS (SELECT pr FROM PostReport pr WHERE pr.post = p AND pr.user.id = :userId)")
    Page<Post> findByCategoryExcludingReportedPosts(@Param("category") PostCategory category,
                                                    @Param("userId") Long userId, Pageable pageable);
    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.isDeleted = false " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM PostReport pr WHERE pr.post = p AND pr.user.id = :userId" +
            ")")
    Page<Post> findAllVisiblePostsExcludingReportedByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p "+
            "JOIN FETCH p.user " +
            "WHERE p.category = :category AND p.isDeleted = false")
    Page<Post> findByCategoryExcludingDeletedPosts(@Param("category") PostCategory category, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.isDeleted = false")
    Page<Post> findAllActivePosts(Pageable pageable);

    List<Post> findByIdIn(List<Long> ids);

    Page<Post> findPostByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

}

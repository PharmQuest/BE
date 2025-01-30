package com.pharmquest.pharmquest.domain.post.repository.post;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom, JpaSpecificationExecutor<Post> {


    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isDeleted = false " +
            "AND p.id NOT IN (SELECT pr.post.id FROM PostReport pr WHERE pr.user.id = :userId)")
    Page<Post> findByCategoryExcludingReportedPosts(@Param("category") PostCategory category,
                                                    @Param("userId") Long userId,
                                                    Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isDeleted = false")
    Page<Post> findByCategoryExcludingDeletedPosts(@Param("category") PostCategory category, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    Page<Post> findAllActivePosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.id NOT IN " +
            "(SELECT pr.post.id FROM PostReport pr WHERE pr.user.id = :userId)")
    Page<Post> findAllVisiblePostsExcludingReportedByUser(@Param("userId") Long userId, Pageable pageable);

}

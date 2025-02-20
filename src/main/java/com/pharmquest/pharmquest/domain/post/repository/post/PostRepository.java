package com.pharmquest.pharmquest.domain.post.repository.post;

import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    List<Post> findByIdIn(List<Long> ids);

    Page<Post> findPostByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :postId")
    Optional<Post> findWithCommentsById(@Param("postId") Long postId);

    @Query("""
    SELECT p FROM Post p
    LEFT JOIN FETCH p.comments
    LEFT JOIN FETCH p.scraps s
    LEFT JOIN FETCH s.user
    LEFT JOIN FETCH p.likes l
    LEFT JOIN FETCH p.reports r
    LEFT JOIN FETCH p.best b
    WHERE p.id = :postId
""")
    Optional<Post> findWithCommentsScrapsLikesById(@Param("postId") Long postId);


}

package com.pharmquest.pharmquest.domain.post.repository.comment;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostAndParentIsNull(Post post, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<Comment> findActiveCommentsByUserOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);



    @Query("""

            SELECT c FROM Comment c
JOIN c.post p
JOIN p.user pu
LEFT JOIN c.parent pr
WHERE\s
    (pu.id = :userId AND c.user.id <> :userId AND c.isDeleted = false)
    OR\s
    (pr IS NOT NULL\s
     AND pr.user.id = :userId\s
     AND c.user.id <> :userId\s
     AND c.isDeleted = false\s
     AND pr.isDeleted = false)
ORDER BY c.createdAt DESC
    """)
    Page<Comment> findUserRelatedComments(
            @Param("userId") Long userId,
            Pageable pageable
    );


}
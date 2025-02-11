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

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.isDeleted = false")
    Page<Comment> findActiveCommentsByUser(@Param("userId") Long userId, Pageable pageable);



    @Query("SELECT c FROM Comment c " +
            "WHERE " +
            "(c.post.user.id = :userId AND c.user.id != :userId) " + // 내 게시글에 남이 단 댓글
            "OR " +
            "(c.parent.user.id = :userId AND c.user.id != :userId) " + // 내 댓글에 남이 단 대댓글 (어떤 게시글이든)
            "ORDER BY c.createdAt ASC")
    Page<Comment> findUserRelatedComments(
            @Param("userId") Long userId,
            Pageable pageable
    );


}

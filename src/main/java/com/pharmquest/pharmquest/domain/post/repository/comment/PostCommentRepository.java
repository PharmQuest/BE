package com.pharmquest.pharmquest.domain.post.repository.comment;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<Comment, Long> {

//    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.children WHERE c.post = :post")
//    List<Comment> findByPost(@Param("post") Post post);

    Page<Comment> findByPostAndParentIsNull(Post post, Pageable pageable);

}

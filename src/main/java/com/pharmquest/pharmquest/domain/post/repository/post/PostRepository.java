package com.pharmquest.pharmquest.domain.post.repository.post;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Page<Post> findByCategory(PostCategory category, Pageable pageable);

    Optional<Post> findById(Long postId);

}

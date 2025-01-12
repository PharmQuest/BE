package com.pharmquest.pharmquest.post.repository;

import com.pharmquest.pharmquest.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

}

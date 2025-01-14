package com.pharmquest.pharmquest.domain.post.repository;

import com.pharmquest.pharmquest.domain.post.data.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

}

package com.pharmquest.pharmquest.domain.post.repository.like;

import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>{

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}

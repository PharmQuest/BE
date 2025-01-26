package com.pharmquest.pharmquest.domain.post.repository.like;

import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>{

    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long memberId);

}

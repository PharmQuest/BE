package com.pharmquest.pharmquest.domain.post.repository.like;

import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>{

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    // 특정 게시물의 좋아요 개수 조회
    int countByPostId(Long postId);
}

package com.pharmquest.pharmquest.domain.post.repository.scrap;

import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<PostScrap> findByPostIdAndUserId(Long postId, Long userId);
    Page<PostScrap> findPostByUserId(Long userId, Pageable pageable);


}
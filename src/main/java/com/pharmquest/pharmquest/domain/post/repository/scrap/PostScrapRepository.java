package com.pharmquest.pharmquest.domain.post.repository.scrap;

import com.pharmquest.pharmquest.mypage.domain.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

}
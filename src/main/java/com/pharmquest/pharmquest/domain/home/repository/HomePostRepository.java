package com.pharmquest.pharmquest.domain.home.repository;

import com.pharmquest.pharmquest.domain.post.data.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HomePostRepository extends JpaRepository<Post, Long> {

    // 인기 게시물 조회
    @Query("select ps.post post from PostScrap ps " +
            "group by post " +
            "order by count(ps) desc " +
            "limit 1")
    Optional<Post> findHotPost();

    // 최신 게시글 5개 조회
    List<Post> findTop5ByOrderByCreatedAt();

}

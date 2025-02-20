package com.pharmquest.pharmquest.domain.post.repository.report;

import com.pharmquest.pharmquest.domain.post.data.mapping.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<PostReport> findByPostIdAndUserId(Long postId, Long userId);
    int countByPostId(Long postId);

}
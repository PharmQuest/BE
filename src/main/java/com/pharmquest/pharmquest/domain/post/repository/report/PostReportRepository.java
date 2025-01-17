package com.pharmquest.pharmquest.domain.post.repository.report;

import com.pharmquest.pharmquest.domain.post.data.mapping.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

}

package com.pharmquest.pharmquest.domain.post.repository.comment;

import com.pharmquest.pharmquest.domain.post.data.mapping.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    Optional<CommentReport> findByCommentIdAndUserId(Long commentId, Long userId);
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    int countByCommentId(Long commentId);
}
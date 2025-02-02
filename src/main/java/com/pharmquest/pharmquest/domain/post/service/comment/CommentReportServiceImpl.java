package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.converter.PostCommentConverter;
import com.pharmquest.pharmquest.domain.post.data.enums.ReportType;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.data.mapping.CommentReport;
import com.pharmquest.pharmquest.domain.post.repository.comment.CommentReportRepository;
import com.pharmquest.pharmquest.domain.post.repository.comment.PostCommentRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommentHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentReportServiceImpl implements CommentReportService {

    private final CommentReportRepository commentReportRepository;
    private final PostCommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentReport createReport(Long userId, Long commentId, ReportType reportType) {
        if (commentReportRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new IllegalStateException("이미 신고한 게시물 입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_EXIST));

        CommentReport newReport = PostCommentConverter.toCommentReport(user, comment, reportType);
        commentReportRepository.save(newReport);

        int reportCount = commentReportRepository.countByCommentId(commentId);
        if (reportCount >= 30) {
            comment.setDeleted(true);
        }

        return newReport;
    }
}

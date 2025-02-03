package com.pharmquest.pharmquest.domain.post.service.report;

import com.pharmquest.pharmquest.domain.post.converter.PostReportConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.ReportType;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostReport;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.repository.report.PostReportRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReportServiceImpl implements PostReportService {


    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostReportRepository postReportRepository;

    @Override
    public PostReport createReport(Long userId, Long postId, ReportType reportType) {
        if (postReportRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new IllegalStateException("이미 신고한 게시물 입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        PostReport newReport = PostReportConverter.toPostReport(user, post, reportType);
        postReportRepository.save(newReport);

        int reportCount = postReportRepository.countByPostId(postId);
        if (reportCount >= 30) {
            post.setDeleted(true);
        }

        return newReport;
    }
}

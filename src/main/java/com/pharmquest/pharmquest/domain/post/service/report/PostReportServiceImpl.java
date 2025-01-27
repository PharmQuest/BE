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
        Optional<PostReport> postReport =
                postReportRepository.findByPostIdAndUserId(postId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if (postReport.isPresent()) {
            throw new IllegalStateException("이미 신고한 게시물 입니다.");
        }

        PostReport newReport = PostReportConverter.toPostReport(user, post, reportType);
        postReportRepository.save(newReport);

        int reportCount = postReportRepository.countByPostId(postId);

        // 신고 횟수가 30 이상이면 게시글 삭제 처리
        if (reportCount >= 30) {
            post.setDeleted(true);
            postRepository.save(post);
        }
        return newReport;
    }
}

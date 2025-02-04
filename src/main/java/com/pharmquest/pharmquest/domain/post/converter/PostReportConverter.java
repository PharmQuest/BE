package com.pharmquest.pharmquest.domain.post.converter;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.ReportType;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostReport;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;

import java.time.LocalDateTime;

public class PostReportConverter {

    public static PostReport toPostReport(User user, Post post, ReportType reportType) {
        return PostReport.builder()
                .user(user)
                .post(post)
                .type(reportType)
                .build();
    }

    public static PostResponseDTO.CreatePostReportResponseDTO toPostReportDTO(PostReport postReport) {
        return PostResponseDTO.CreatePostReportResponseDTO.builder()
                .postReportId(postReport.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}

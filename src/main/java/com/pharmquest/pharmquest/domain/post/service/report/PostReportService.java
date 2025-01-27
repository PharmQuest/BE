package com.pharmquest.pharmquest.domain.post.service.report;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.ReportType;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostReport;

public interface PostReportService {

    PostReport createReport(Long userId, Long postId, ReportType reportType);
}

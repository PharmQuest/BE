package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.data.enums.ReportType;
import com.pharmquest.pharmquest.domain.post.data.mapping.CommentReport;

public interface CommentReportService {

    CommentReport createReport(Long userId, Long commentId, ReportType reportType);
}

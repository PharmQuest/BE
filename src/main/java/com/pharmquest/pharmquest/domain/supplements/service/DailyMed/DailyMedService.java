package com.pharmquest.pharmquest.domain.supplements.service.DailyMed;

import com.pharmquest.pharmquest.domain.supplements.web.dto.DailyMedResponseDTO;

import java.util.List;

public interface DailyMedService {
    List<DailyMedResponseDTO.ExtractedInfo> extractSupplementInfo();
    DailyMedResponseDTO.DetailInfo getDetailInfo(String setId, String title);
}

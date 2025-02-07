package com.pharmquest.pharmquest.domain.medicine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MedicineListResponseDTO {
    private long totalCount;  // 전체 약물 개수 검색 수
    private List<MedicineResponseDTO> medicines;  // 약물 리스트
}

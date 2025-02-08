package com.pharmquest.pharmquest.domain.medicine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MedicineListPageResponseDTO {
    private long amountCount;  // 전체 개수
    private int amountPage;    // 전체 페이지 수
    private int currentCount;  // 현재 페이지의 개수
    private int currentPage;   // 현재 페이지 번호
    private List<MedicineResponseDTO> medicines; // 약 정보 리스트
}

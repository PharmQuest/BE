package com.pharmquest.pharmquest.domain.medicine.service;

import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;

import java.util.List;

public interface MedicineService {
    List<MedicineResponseDTO> getMedicines(String query, int limit);
    String getTotalData(String query, int limit);
    MedicineResponseDTO getMedicineBySplSetId(String splSetId);
}

package com.pharmquest.pharmquest.medicine.service;

import com.pharmquest.pharmquest.medicine.dto.MedicineResponseDTO;

import java.util.List;

public interface MedicineService {
    List<MedicineResponseDTO> getMedicines(String query, int limit);
}

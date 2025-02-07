package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineDetailResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineListResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineOpenapiResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicineService {
    List<MedicineOpenapiResponseDTO> getMedicines(String query, int limit);
    List<MedicineOpenapiResponseDTO> getMedicinesbyCategory(MedicineCategory category, int limit);
    List<MedicineOpenapiResponseDTO > getEnMedicines(String query, int limit);
    String getTotalData(String query, int limit);
    MedicineDetailResponseDTO getMedicineBySplSetId(String splSetId);
    List<Medicine> saveMedicinesByCategory(MedicineCategory category, int limit);
    List<Medicine> saveOtherMedicines(String query, int limit);

    MedicineDetailResponseDTO getMedicineBySplSetIdFromDB(String splSetId);
    List<MedicineResponseDTO> getMedicinesFromDBByCategory(MedicineCategory category, int page, int size);
    MedicineListResponseDTO searchMedicinesByCategoryAndKeyword(MedicineCategory category, String keyword, int page, int size);

}

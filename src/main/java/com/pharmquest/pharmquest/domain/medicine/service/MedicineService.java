package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.web.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MedicineService {
    List<MedicineOpenapiResponseDTO> getMedicines(String query, int limit);
    List<MedicineOpenapiResponseDTO> getMedicinesbyCategory(MedicineCategory category, int limit);
    List<MedicineOpenapiResponseDTO > getEnMedicines(String query, int limit);
    String getTotalData(String query, int limit);
    MedicineDetailResponseDTO getMedicineBySplSetId(Long userId, String splSetId);
    List<Medicine> saveMedicinesByCategory(MedicineCategory category, int limit);
    List<Medicine> saveOtherMedicines(String query, int limit);

    MedicineDetailResponseDTO getMedicineByIdFromDB(Long userId, Long medicineId) ;
    MedicineListPageResponseDTO getMedicinesFromDBByCategory(Long userId, MedicineCategory category, int page, int size);

    MedicineListPageResponseDTO searchMedicinesByCategoryAndKeyword(Long userId, MedicineCategory category,
                                                                    String keyword, String country, int page, int size);
}

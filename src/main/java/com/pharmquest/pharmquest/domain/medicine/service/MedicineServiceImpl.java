package com.pharmquest.pharmquest.domain.medicine.service;

import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<MedicineResponseDTO> getMedicines(String query, int limit) {
        return medicineRepository.findMedicines(query, limit);
    }
}
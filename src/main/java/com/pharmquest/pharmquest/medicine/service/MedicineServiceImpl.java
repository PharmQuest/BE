package com.pharmquest.pharmquest.medicine.service;

import com.pharmquest.pharmquest.medicine.dto.MedicineResponseDTO;
import com.pharmquest.pharmquest.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.medicine.service.MedicineService;
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
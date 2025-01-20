package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.service.MedicineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

    private final MedicineService medicineService;
    private final MedicineRepository medicineRepository;

    public MedicineController(MedicineService medicineService, MedicineRepository medicineRepository) {
        this.medicineService = medicineService;
        this.medicineRepository = medicineRepository;
    }

    @GetMapping("/search")
    public List<MedicineResponseDTO> searchMedicines(
            @RequestParam(defaultValue="openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getMedicines(query, limit);
    }

    @GetMapping("/total")
    public String viewTotal(
            @RequestParam(defaultValue = "openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "1") int limit) {
        return medicineRepository.findTotal(query, limit);
    }
}


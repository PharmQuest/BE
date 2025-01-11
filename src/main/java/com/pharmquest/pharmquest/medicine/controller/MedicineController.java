package com.pharmquest.pharmquest.medicine.controller;

import com.pharmquest.pharmquest.medicine.dto.MedicineResponseDTO;
import com.pharmquest.pharmquest.medicine.service.MedicineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/search")
    public List<MedicineResponseDTO> searchMedicines(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getMedicines(query, limit);
    }
}


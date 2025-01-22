package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.pharmquest.pharmquest.domain.medicine.service.MedicineService;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    // 번역된 약물 정보 검색
    @GetMapping("/search")
    public List<MedicineResponseDTO> searchMedicines(
            @RequestParam(defaultValue = "openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getMedicines(query, limit);
    }

    // 번역되지 않은 약물 정보 검색
    @GetMapping("/search/english")
    public List<MedicineResponseDTO> searchEnMedicines(
            @RequestParam(defaultValue = "openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getEnMedicines(query, limit);
    }

    // FDA API에서 전체 데이터 원본 반환
    @GetMapping("/total")
    public String viewTotal(
            @RequestParam(defaultValue = "openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "1") int limit) {
        return medicineService.getTotalData(query, limit);
    }

    // SPL Set ID로 약물 세부 정보 검색
    @GetMapping("/detail")
    public MedicineResponseDTO searchBySplSetId(@RequestParam String splSetId) {
        return medicineService.getMedicineBySplSetId(splSetId);
    }
}

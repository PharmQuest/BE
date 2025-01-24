package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.service.MedicineService;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

    private final MedicineService medicineService;
    private final MedRepository medRepository;

    public MedicineController(MedicineService medicineService, MedRepository medrepository) {
        this.medicineService = medicineService;
        this.medRepository =medrepository;
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




    @Operation(summary = "Save a new medicine", description = "Add a new medicine by providing its content.")
    @PostMapping("/{content}")
    public Medicine saveMedicine(@PathVariable String content) {
        Medicine medicine = new Medicine();
        medicine.setContent(content);
        return medRepository.save(medicine); // DB에 저장
    }

    @Operation(summary = "Get all medicines", description = "Retrieve a list of all medicines.")
    // 전체 약물 조회 API
    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medRepository.findAll(); // DB에서 전체 데이터 조회
    }
}

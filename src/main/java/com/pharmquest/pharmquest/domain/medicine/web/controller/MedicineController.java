package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.service.MedicineService;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineDetailResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

    private final MedicineService medicineService;
    private final MedRepository medRepository;


    public MedicineController(MedicineService medicineService, MedRepository medRepository) {
        this.medicineService = medicineService;
        this.medRepository = medRepository;
    }

    // 번역된 약물 정보 검색
    @GetMapping("test/search/ko")
    public List<MedicineResponseDTO> searchMedicines(
            @RequestParam(defaultValue = "openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getMedicines(query, limit);
    }

    @GetMapping("test/lists")
    public List<MedicineResponseDTO> searchMedicinesByCategory(
            @RequestParam(defaultValue = "진통/해열") String category,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getMedicinesbyCategory(category, limit);
    }

    @GetMapping("/lists")
    public List<MedicineResponseDTO> searchMedicinesByCategory(
            @RequestParam(defaultValue = "전체") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return medicineService.getMedicinesFromDBByCategory(category, page, size);
    }

    // 번역되지 않은 약물 정보 검색
    @GetMapping("test/search/english")
    public List<MedicineResponseDTO> searchEnMedicines(
            @RequestParam(defaultValue = "openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getEnMedicines(query, limit);
    }

    // FDA API에서 전체 데이터 원본 반환
    @GetMapping("test/total")
    public String viewTotal(
            @RequestParam(defaultValue = "openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "1") int limit) {
        return medicineService.getTotalData(query, limit);
    }

    // SPL Set ID로 약물 세부 정보 검색
    @GetMapping("test/detail")
    public MedicineDetailResponseDTO searchBySplSetId(@RequestParam String splSetId) {
        return medicineService.getMedicineBySplSetId(splSetId);
    }

    @GetMapping("/detail")
    public MedicineDetailResponseDTO searchBySplSetIdFromDB(@RequestParam String splSetId) {
        return medicineService.getMedicineBySplSetIdFromDB(splSetId);
    }

    //rds 연결 확인용
//    @Operation(summary = "Save a new medicine", description = "Add a new medicine by providing its content.")
//    @PostMapping("/{content}")
//    public Medicine saveMedicine(@PathVariable String content) {
//        Medicine medicine = new Medicine();
//        medicine.setContent(content);
//        return medRepository.save(medicine); // DB에 저장
//    }

    @Operation(summary = "FDA API 데이터를 DB에 저장", description = "FDA API에서 기타 제외 카테고리 약물 정보를 받아와 DB에 저장합니다.")
    @PostMapping("/save")
    public List<Medicine> saveMedicineByCategory(
            @RequestParam(defaultValue = "진통/해열") String category,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.saveMedicinesByCategory(category, limit);
    }

    @Operation(summary = "기타 항목 FDA API 데이터를 DB에 저장", description = "FDA API에서 기타 카테고리 약물 정보를 받아와 DB에 저장합니다.")
    @PostMapping("/save/other")
    public ResponseEntity<List<Medicine>> saveOtherMedicines(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        List<Medicine> savedMedicines = medicineService.saveOtherMedicines(query, limit);
        return ResponseEntity.ok(savedMedicines);
    }
    @Operation(summary = "Get all medicines", description = "Retrieve a list of all medicines.")
    // 전체 약물 조회 API
    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medRepository.findAll(); // DB에서 전체 데이터 조회
    }

}

package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.service.MedicineService;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineDetailResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineListResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineOpenapiResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.code.status.SuccessStatus;
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
    @GetMapping("test/searchOpenAPI/ko")
    public List<MedicineOpenapiResponseDTO> searchMedicines(
            @RequestParam(defaultValue = "openfda.product_type:OTC") String query,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getMedicines(query, limit);
    }

    @GetMapping("test/lists")
    public List<MedicineOpenapiResponseDTO> searchMedicinesByCategory(
            @RequestParam(defaultValue = "진통/해열") String category,
            @RequestParam(defaultValue = "10") int limit) {
        return medicineService.getMedicinesbyCategory(category, limit);
    }

    @Operation(summary = "카테고리별 약물 검색", description = "DB에서 특정 카테고리별로 약물을 검색합니다.")
    @GetMapping("/lists")
    public ResponseEntity<ApiResponse<List<MedicineResponseDTO>>> searchMedicinesByCategory(
            @RequestParam(defaultValue = "전체") String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<MedicineResponseDTO> medicines = medicineService.getMedicinesFromDBByCategory(category, page - 1, size);
            return ApiResponse.onSuccess(SuccessStatus.MEDICINE_FETCH_SUCCESS, medicines);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.MEDICINE_NOT_FOUND);
        }
    }

    // 번역되지 않은 약물 정보 검색
    @GetMapping("test/searchOpenAPI/english")
    public List<MedicineOpenapiResponseDTO> searchEnMedicines(
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

    @Operation(summary = "SPL Set ID를 이용한 약물 상세 검색", description = "DB에서 SPL Set ID를 기반으로 약물 정보를 조회합니다.")
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<MedicineDetailResponseDTO>> searchBySplSetIdFromDB(@RequestParam String splSetId) {
        try {
            MedicineDetailResponseDTO medicine = medicineService.getMedicineBySplSetIdFromDB(splSetId);
            return ApiResponse.onSuccess(SuccessStatus.MEDICINE_FETCH_SUCCESS, medicine);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.MEDICINE_NOT_FOUND);
        }
    }


    @Operation(summary = "FDA API 데이터를 DB에 저장", description = "FDA API에서 특정 카테고리의 약물 정보를 받아와 DB에 저장합니다.")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<List<Medicine>>> saveMedicineByCategory(
            @RequestParam(defaultValue = "진통/해열") String category,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Medicine> savedMedicines = medicineService.saveMedicinesByCategory(category, limit);
            return ApiResponse.onSuccess(SuccessStatus.MEDICINE_SAVE_SUCCESS, savedMedicines);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.MEDICINE_SAVE_FAILED);
        }
    }

    @Operation(summary = "기타 카테고리 약물 DB 저장", description = "FDA API에서 기타 카테고리 약물 정보를 받아와 DB에 저장합니다.")
    @PostMapping("/save/other")
    public ResponseEntity<ApiResponse<List<Medicine>>> saveOtherMedicines(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Medicine> savedMedicines = medicineService.saveOtherMedicines(query, limit);
            return ApiResponse.onSuccess(SuccessStatus.MEDICINE_SAVE_SUCCESS, savedMedicines);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.MEDICINE_SAVE_FAILED);
        }
    }

    @Operation(summary = "Get all medicines", description = "Retrieve a list of all medicines.")
    // 전체 약물 조회 API
    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medRepository.findAll(); // DB에서 전체 데이터 조회
    }

    @Operation(summary = "약물 검색 API", description = "카테고리 및 키워드를 이용해 DB에서 약물을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<MedicineListResponseDTO>> searchMedicinesByCategoryAndKeyword(
            @RequestParam(defaultValue = "전체") String category,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            MedicineListResponseDTO medicines = medicineService.searchMedicinesByCategoryAndKeyword(category, keyword, page - 1, size);
            return ApiResponse.onSuccess(SuccessStatus.MEDICINE_FETCH_SUCCESS, medicines);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.MEDICINE_NOT_FOUND);
        }
    }


}

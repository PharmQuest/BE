package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.converter.MedicineConverter;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineConverter medicineConverter;

    public MedicineServiceImpl(MedicineRepository medicineRepository, MedicineConverter medicineConverter) {
        this.medicineRepository = medicineRepository;
        this.medicineConverter = medicineConverter;
    }

    // 전체 정보 확인용 (FDA API 데이터를 원본 JSON 문자열로 반환) 백엔드 작업용
    @Override
    public String getTotalData(String query, int limit) {
        return medicineRepository.fetchMedicineData(query, limit);
    }

    // FDA API 데이터를 DTO로 변환 (번역 포함)
    @Override
    public List<MedicineResponseDTO> getMedicines(String query, int limit) {
        try {
            String response = medicineRepository.fetchMedicineData(query, limit);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            List<MedicineResponseDTO> medicines = new ArrayList<>();
            if (results.isArray()) {
                for (JsonNode result : results) {
                    medicines.add(medicineConverter.convertWithTranslation(result));
                }
            }
            return medicines;
        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }

    // FDA API 데이터를 DTO로 변환 (번역 없이 원본 반환) 백엔드 작업용
    @Override
    public List<MedicineResponseDTO> getEnMedicines(String query, int limit) {
        try {
            String response = medicineRepository.fetchMedicineData(query, limit);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            List<MedicineResponseDTO> medicines = new ArrayList<>();
            if (results.isArray()) {
                for (JsonNode result : results) {
                    medicines.add(medicineConverter.convertWithoutTranslation(result));
                }
            }
            return medicines;
        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }

    // SPL Set ID로 약물 데이터를 조회
    @Override
    public MedicineResponseDTO getMedicineBySplSetId(String splSetId) {
        try {
            String query = "openfda.spl_set_id:" + splSetId;
            String response = medicineRepository.fetchMedicineData(query, 1);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            if (results.isArray() && results.size() > 0) {
                return medicineConverter.convertWithTranslation(results.get(0));
            } else {
                throw new IllegalArgumentException("해당 spl_set_id를 가진 약물이 없습니다: " + splSetId);
            }
        } catch (Exception e) {
            throw new RuntimeException("spl_set_id로 약물 검색 중 오류 발생", e);
        }
    }
}
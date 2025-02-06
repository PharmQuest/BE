package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.converter.MedicineConverter;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineDetailResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineListResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineOpenapiResponseDTO;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineConverter medicineConverter;
    private final MedRepository medRepository;

    public MedicineServiceImpl(MedicineRepository medicineRepository, MedicineConverter medicineConverter,MedRepository medRepository) {
        this.medicineRepository = medicineRepository;
        this.medicineConverter = medicineConverter;
        this.medRepository = medRepository;
    }

    // 전체 정보 확인용 (FDA API 데이터를 원본 JSON 문자열로 반환) 백엔드 작업용
    @Override
    public String getTotalData(String query, int limit) {
        return medicineRepository.fetchMedicineData(query, limit);
    }

    // FDA API 데이터를 DTO로 변환 (번역 포함)
    @Override
    public List<MedicineOpenapiResponseDTO> getMedicines(String query, int limit) {
        try {
            String response = medicineRepository.fetchMedicineData(query, limit);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            List<MedicineOpenapiResponseDTO> medicines = new ArrayList<>();
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

    @Override
    public List<MedicineOpenapiResponseDTO> getMedicinesbyCategory(String query, int limit) {
        try {
            // 카테고리 이름을 쿼리로 변환 (해당되는 경우)
            String apiQuery = MedicineCategoryMapper.getQueryForCategory(query);
            if (apiQuery != null) {
                query = apiQuery; // FDA 쿼리로 대체
            }

            // 더 많은 데이터를 요청 (limit * 2)
            int requestLimit = limit * 3;
            String response = medicineRepository.fetchMedicineData(query, requestLimit);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            List<MedicineOpenapiResponseDTO > medicines = new ArrayList<>();
            if (results.isArray()) {
                for (JsonNode result : results) {
                    MedicineOpenapiResponseDTO  dto = medicineConverter.convertWithTranslation(result);
                    if (isValidMedicine(dto)) {
                        medicines.add(dto);
                    }
                }
            }

            // 필터링된 데이터 중 상위 limit 개수만 반환
            return medicines.size() > limit ? medicines.subList(0, limit) : medicines;
        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패", e);
        }
    }

    // FDA API 데이터를 DTO로 변환 (번역 없이 원본 반환) 백엔드 작업용
    @Override
    public List<MedicineOpenapiResponseDTO> getEnMedicines(String query, int limit) {
        try {
            String response = medicineRepository.fetchMedicineData(query, limit);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            List<MedicineOpenapiResponseDTO> medicines = new ArrayList<>();
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
    public MedicineDetailResponseDTO getMedicineBySplSetId(String splSetId) {
        try {
            // splSetId로 FDA API 요청
            String query = "openfda.spl_set_id:" + splSetId;
            String response = medicineRepository.fetchMedicineData(query, 1);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            // 결과가 있을 경우 변환
            if (results.isArray() && results.size() > 0) {
                return medicineConverter.convertToDetail(results.get(0));
            } else {
                throw new IllegalArgumentException("해당 splSetId를 가진 약물이 없습니다: " + splSetId);
            }
        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패: 약물 상세 정보 조회 중 오류 발생", e);
        }
    }

    @Override
    public List<MedicineResponseDTO> getMedicinesFromDBByCategory(String category, int page, int size) {
        try {
            Page<Medicine> medicinesPage;
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending()); // 페이지네이션 설정 (ID 기준 정렬)

            // "전체" 선택 시 모든 데이터 페이지네이션 처리
            if (category.equalsIgnoreCase("전체")) {
                medicinesPage = medRepository.findAll(pageable);
            } else {
                medicinesPage = medRepository.findByCategoryIgnoreCase(category, pageable);
            }

            // 로그 추가 (검색 결과 없을 경우 디버깅)
            if (medicinesPage.isEmpty()) {
                System.out.println("카테고리 '" + category + "' 에 해당하는 데이터가 없습니다.");
            }

            // 결과 변환 후 반환
            return medicinesPage.getContent().stream()
                    .map(medicineConverter::convertFromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("DB에서 약물 데이터를 가져오는 중 오류 발생", e);
        }
    }

    @Override
    public MedicineDetailResponseDTO getMedicineBySplSetIdFromDB(String splSetId) {
        try {
            // DB에서 SPL Set ID를 기준으로 약물 조회
            Medicine medicine = medRepository.findBySplSetId(splSetId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 SPL Set ID를 가진 약물이 없습니다: " + splSetId));

            // 엔티티를 DTO로 변환하여 반환
            return new MedicineDetailResponseDTO(
                    medicine.getBrandName(),
                    medicine.getGenericName(),
                    medicine.getSubstanceName(),
                    medicine.getActiveIngredient(),
                    medicine.getPurpose(),
                    medicine.getIndicationsAndUsage(),
                    medicine.getDosageAndAdministration(),
                    medicine.getSplSetId(),
                    medicine.getImgUrl(),
                    medicine.getCategory(),
                    medicine.getCountry()
            );
        } catch (Exception e) {
            throw new RuntimeException("DB에서 약물 세부 정보를 가져오는 중 오류 발생", e);
        }
    }



    private boolean isValidMedicine(MedicineOpenapiResponseDTO  dto) {
        return dto.getBrandName() != null && !dto.getBrandName().isEmpty()
                && dto.getGenericName() != null && !dto.getGenericName().isEmpty()
                && dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()
                && dto.getCategory() != null && !dto.getCategory().isEmpty();
    }

    private boolean isValidMedicineDetail(MedicineDetailResponseDTO dto) {
        return dto.getBrandName() != null && !dto.getBrandName().isEmpty()
                && dto.getGenericName() != null && !dto.getGenericName().isEmpty()
                && dto.getSubstanceName() != null && !dto.getSubstanceName().isEmpty()
                && dto.getActiveIngredient() != null && !dto.getActiveIngredient().isEmpty()
                && dto.getPurpose() != null && !dto.getPurpose().isEmpty()
                && dto.getIndicationsAndUsage() != null && !dto.getIndicationsAndUsage().isEmpty()
                && dto.getDosageAndAdministration() != null && !dto.getDosageAndAdministration().isEmpty()
                && dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()
                && dto.getCategory() != null && !dto.getCategory().isEmpty();
    }

    //카테고리 별 db 저장 로직
    @Transactional
    public List<Medicine> saveMedicinesByCategory(String category, int limit) {
        try {
            String query = MedicineCategoryMapper.getQueryForCategory(category);
            if (query == null) {
                throw new IllegalArgumentException("해당하는 카테고리가 없습니다: " + category);
            }

            List<Medicine> savedMedicines = new ArrayList<>();
            int requestLimit = limit * 3; // 더 많은 데이터 요청
            int retryCount = 0;

            while (savedMedicines.size() < limit && retryCount < 3) { // 최대 3번 추가 요청
                String response = medicineRepository.fetchMedicineData(query, requestLimit);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode results = mapper.readTree(response).path("results");

                if (results.isArray()) {
                    for (JsonNode result : results) {
                        String splSetId = result.at("/openfda/spl_set_id").asText("Unknown");

                        // 이미 존재하는 데이터 건너뛰기
                        if (medRepository.existsBySplSetId(splSetId)) {
                            continue;
                        }

                        MedicineDetailResponseDTO dto = medicineConverter.convertToDetail(result);

                        if (isValidMedicineDetail(dto)) {
                            Medicine medicine = new Medicine();
                            medicine.setBrandName(dto.getBrandName());
                            medicine.setGenericName(dto.getGenericName());
                            medicine.setSubstanceName(dto.getSubstanceName());
                            medicine.setActiveIngredient(dto.getActiveIngredient());
                            medicine.setPurpose(dto.getPurpose());
                            medicine.setIndicationsAndUsage(dto.getIndicationsAndUsage());
                            medicine.setDosageAndAdministration(dto.getDosageAndAdministration());
                            medicine.setSplSetId(dto.getSplSetId());
                            medicine.setImgUrl(dto.getImgUrl());
                            medicine.setCategory(dto.getCategory());
                            medicine.setCountry(dto.getCountry());

                            savedMedicines.add(medRepository.save(medicine));
                        }

                        if (savedMedicines.size() >= limit) break;
                    }
                }

                retryCount++;
            }

            // 최소 개수 미달 시 예외 처리
            if (savedMedicines.size() < limit) {
                throw new RuntimeException("충분한 데이터를 저장하지 못했습니다. (현재 개수: " + savedMedicines.size() + ")");
            }

            return savedMedicines;
        } catch (Exception e) {
            throw new RuntimeException("FDA API 데이터를 저장하는 중 오류 발생", e);
        }
    }
    //기타항목 저장 카테고리 연산량이 너무 많아서 따로 로직 설정
    @Transactional
    public List<Medicine> saveOtherMedicines(String query, int limit) {
        try {
            if (query == null || query.trim().isEmpty()) {
                throw new IllegalArgumentException("Query 파라미터가 필요합니다.");
            }

            List<Medicine> savedMedicines = new ArrayList<>();
            int requestLimit = 10;  // 한 번 요청할 최대 개수
            int retryCount = 0;
            int totalFetched = 0;  // 가져온 전체 개수

            while (savedMedicines.size() < limit && retryCount < 3) { // 최대 5번 재시도
                String response = medicineRepository.fetchMedicineData(query, requestLimit);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode results = mapper.readTree(response).path("results");

                if (results.isArray()) {
                    for (JsonNode result : results) {
                        String splSetId = result.at("/openfda/spl_set_id").asText("Unknown");

                        if (medRepository.existsBySplSetId(splSetId)) {
                            continue;
                        }

                        MedicineDetailResponseDTO dto = medicineConverter.convertToDetail(result);

                        if (isValidMedicineDetail(dto)) {
                            Medicine medicine = new Medicine();
                            medicine.setBrandName(dto.getBrandName());
                            medicine.setGenericName(dto.getGenericName());
                            medicine.setSubstanceName(dto.getSubstanceName());
                            medicine.setActiveIngredient(dto.getActiveIngredient());
                            medicine.setPurpose(dto.getPurpose());
                            medicine.setIndicationsAndUsage(dto.getIndicationsAndUsage());
                            medicine.setDosageAndAdministration(dto.getDosageAndAdministration());
                            medicine.setSplSetId(dto.getSplSetId());
                            medicine.setImgUrl(dto.getImgUrl());
                            medicine.setCategory("기타");
                            medicine.setCountry(dto.getCountry());

                            savedMedicines.add(medRepository.save(medicine));
                        }

                        if (savedMedicines.size() >= limit) break;
                    }
                }

                totalFetched += requestLimit; // 가져온 데이터 개수 업데이트
                retryCount++;

                if (totalFetched >= 500) break; // 500개 이상 조회되면 중단
            }

            if (savedMedicines.size() < limit) {
                throw new RuntimeException("충분한 데이터를 저장하지 못했습니다. (현재 개수: " + savedMedicines.size() + ")");
            }

            return savedMedicines;
        } catch (Exception e) {
            throw new RuntimeException("FDA API 데이터를 저장하는 중 오류 발생", e);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public MedicineListResponseDTO searchMedicinesByCategoryAndKeyword(String category, String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            Page<Medicine> medicinesPage;

            if (category.equalsIgnoreCase("전체")) {
                if (keyword != null && !keyword.isEmpty()) {
                    medicinesPage = medRepository.findByKeyword(keyword, pageable);
                } else {
                    medicinesPage = medRepository.findAll(pageable);
                }
            } else {
                if (keyword != null && !keyword.isEmpty()) {
                    medicinesPage = medRepository.findByCategoryAndKeyword(category, keyword, pageable);
                } else {
                    medicinesPage = medRepository.findByCategoryIgnoreCase(category, pageable);
                }
            }

            long totalCount = medicinesPage.getTotalElements();  // 전체 개수 가져오기

            List<MedicineResponseDTO> medicines = medicinesPage.getContent().stream()
                    .map(medicineConverter::convertFromEntity)
                    .collect(Collectors.toList());

            return new MedicineListResponseDTO(totalCount, medicines);
        } catch (Exception e) {
            throw new RuntimeException("DB에서 약물 데이터를 검색하는 중 오류 발생", e);
        }
    }


}
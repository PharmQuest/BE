package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.converter.KoreanMedicineConverter;
import com.pharmquest.pharmquest.domain.medicine.converter.MedicineConverter;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.MedicineCategoryMapper;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.repository.KoreanMedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineScrapRepository;
import com.pharmquest.pharmquest.domain.medicine.web.dto.*;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineConverter medicineConverter;
    private final MedRepository medRepository;
    private final MedicineScrapRepository scrapRepository;
    private final UserRepository userRepository;

    public MedicineServiceImpl(MedicineRepository medicineRepository, MedicineConverter medicineConverter,MedRepository medRepository, MedicineScrapRepository scrapRepository,UserRepository userRepository) {
        this.medicineRepository = medicineRepository;
        this.medicineConverter = medicineConverter;
        this.medRepository = medRepository;
        this.scrapRepository = scrapRepository;
        this.userRepository = userRepository;
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
    public List<MedicineOpenapiResponseDTO> getMedicinesbyCategory(MedicineCategory category, int limit) {
        try {
            // ✅ Enum을 기반으로 FDA API 검색 쿼리 가져오기
            String query = MedicineCategoryMapper.getQueryForCategory(category);

            // 더 많은 데이터를 요청 (limit * 3)
            int requestLimit = limit * 3;
            String response = medicineRepository.fetchMedicineData(query, requestLimit);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            List<MedicineOpenapiResponseDTO> medicines = new ArrayList<>();
            if (results.isArray()) {
                for (JsonNode result : results) {
                    MedicineOpenapiResponseDTO dto = medicineConverter.convertWithTranslation(result);
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
    public MedicineDetailResponseDTO getMedicineBySplSetId(Long userId, String splSetId) {
        try {
            // splSetId로 FDA API 요청
            String query = "openfda.spl_set_id:" + splSetId;
            String response = medicineRepository.fetchMedicineData(query, 1);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response).path("results");

            // 결과가 있을 경우 변환
            if (results.isArray() && results.size() > 0) {
                return medicineConverter.convertToDetail(results.get(0),userId);
            } else {
                throw new IllegalArgumentException("해당 splSetId를 가진 약물이 없습니다: " + splSetId);
            }
        } catch (Exception e) {
            throw new RuntimeException("FDA API 요청 실패: 약물 상세 정보 조회 중 오류 발생", e);
        }
    }

    @Override
    public MedicineListPageResponseDTO getMedicinesFromDBByCategory(Long userId, MedicineCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Medicine> medicinesPage = (category == MedicineCategory.ALL)
                ? medRepository.findAll(pageable)
                : medRepository.findByCategory(category, pageable);

        long amountCount = medicinesPage.getTotalElements(); // 전체 개수
        int amountPage = medicinesPage.getTotalPages();      // 전체 페이지 수
        int currentCount = medicinesPage.getNumberOfElements(); // 현재 페이지의 개수
        int currentPage = medicinesPage.getNumber()+1;         // 현재 페이지 번호

        List<MedicineResponseDTO> medicines = medicinesPage.getContent().stream()
                .map(medicine -> medicineConverter.convertFromEntity(medicine, userId))
                .collect(Collectors.toList());

        return new MedicineListPageResponseDTO(amountCount, amountPage, currentCount, currentPage, medicines);
    }

    @Override
    public MedicineDetailResponseDTO getMedicineByIdFromDB(Long userId, Long medicineId) {
        try {
            // DB에서 medicineTableId를 기준으로 약물 조회
            Medicine medicine = medRepository.findById(medicineId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 약물이 없습니다: " + medicineId));

            String category = MedicineCategoryMapper.toKoreanCategory(medicine.getCategory());

            boolean isScrapped = false;
            if (userId != null) { // 로그인한 경우만 스크랩 여부 확인
                isScrapped = scrapRepository.existsByUserAndMedicine(
                        userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다.")),
                        medicine
                );
            }

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
                    category,
                    medicine.getCountry(),
                    medicine.getWarnings(),
                    isScrapped
            );

        } catch (Exception e) {
            throw new RuntimeException("DB에서 약물 세부 정보를 가져오는 중 오류 발생", e);
        }
    }



    private boolean isValidMedicine(MedicineOpenapiResponseDTO  dto) {
        return dto.getBrandName() != null && !dto.getBrandName().isEmpty()
                && dto.getGenericName() != null && !dto.getGenericName().isEmpty()
                && dto.getImgUrl() != null && !dto.getImgUrl().isEmpty();
    }

    private boolean isValidMedicineDetail(MedicineDetailResponseDTO dto) {
        return isValid(dto.getBrandName()) &&
                isValid(dto.getGenericName()) &&
                isValid(dto.getSubstanceName()) &&
                isValid(dto.getActiveIngredient()) &&
                isValid(dto.getPurpose()) &&
                isValid(dto.getIndicationsAndUsage()) &&
                isValid(dto.getDosageAndAdministration()) &&
                isValid(dto.getImgUrl()) &&
                isValid(dto.getWarnings());
    }

    private boolean isValidSaveMedicineDetail(MedicineSaveDetailResponseDTO dto) {
        return isValid(dto.getBrandName()) &&
                isValid(dto.getGenericName()) &&
                isValid(dto.getSubstanceName()) &&
                isValid(dto.getActiveIngredient()) &&
                isValid(dto.getPurpose()) &&
                isValid(dto.getIndicationsAndUsage()) &&
                isValid(dto.getDosageAndAdministration()) &&
                isValid(dto.getImgUrl()) &&
                isValid(dto.getWarnings());
    }

    // 공통된 유효성 검사 로직
    private boolean isValid(String value) {
        return value != null && !value.isEmpty() && !value.equalsIgnoreCase("Unknown") && !value.equals("알려지지 않은");
    }


    //카테고리 별 db 저장 로직
    @Transactional
    public List<Medicine> saveMedicinesByCategory(MedicineCategory category, int limit) {
        try {
            // ✅ Enum 기반으로 검색 쿼리 가져오기 (한글 제거)
            String query = MedicineCategoryMapper.getQueryForCategory(category);

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

                        //  이미 존재하는 데이터는 건너뛰기
                        if (medRepository.existsBySplSetId(splSetId)) {
                            continue;
                        }

                        MedicineSaveDetailResponseDTO dto = medicineConverter.SaveConvertToDetail(result, null);

                        if (isValidSaveMedicineDetail(dto)) {
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
                            medicine.setCategory(category); //  Enum 그대로 적용
                            medicine.setCountry(dto.getCountry());
                            medicine.setWarnings(dto.getWarnings());

                            savedMedicines.add(medRepository.save(medicine));
                        }

                        if (savedMedicines.size() >= limit) break;
                    }
                }

                retryCount++;
            }

            //  최소 개수 미달 시 예외 처리
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

            while (savedMedicines.size() < limit && retryCount < 3) { // 최대 3번 재시도
                String response = medicineRepository.fetchMedicineData(query, requestLimit);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode results = mapper.readTree(response).path("results");

                if (results.isArray()) {
                    for (JsonNode result : results) {
                        String splSetId = result.at("/openfda/spl_set_id").asText("Unknown");

                        if (medRepository.existsBySplSetId(splSetId)) {
                            continue;
                        }

                        MedicineSaveDetailResponseDTO dto = medicineConverter.SaveConvertToDetail(result, null);

                        if (isValidSaveMedicineDetail(dto)) {
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
                            medicine.setCategory(MedicineCategory.OTHER); //  Enum 적용
                            medicine.setCountry(dto.getCountry());
                            medicine.setWarnings(dto.getWarnings());

                            savedMedicines.add(medRepository.save(medicine));
                        }

                        if (savedMedicines.size() >= limit) break;
                    }
                }

                totalFetched += requestLimit;
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
    public MedicineListPageResponseDTO searchMedicinesByCategoryAndKeyword(Long userId, MedicineCategory category,
                                                                           String keyword, String country, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            Page<Medicine> medicinesPage;

            log.info("🔹 검색 요청 - 카테고리: {}, 키워드: {}, 국가: {}", category, keyword, country);

            // ✅ 1. 키워드가 없는 경우 → 카테고리와 국가만 필터링
            if (keyword == null || keyword.trim().isEmpty()) {
                medicinesPage = (category == MedicineCategory.ALL)
                        ? medRepository.findAll(pageable)
                        : medRepository.findByCategoryAndCountry(category, country, pageable);
            }
            // ✅ 2. 키워드가 있는 경우 → 카테고리 + 키워드 + 국가 필터링
            else {
                medicinesPage = (category == MedicineCategory.ALL)
                        ? medRepository.findByKeywordAndCountry(keyword, country, pageable)
                        : medRepository.findByCategoryKeywordAndCountry(category, keyword, country, pageable);
            }

            // ✅ 3. 전체 개수 계산
            long totalCountBeforeFiltering = medicinesPage.getTotalElements();
            log.info("🔹 초기 검색 결과 개수 (카테고리 & 키워드 필터링 적용 후): {}", totalCountBeforeFiltering);

            // ✅ 4. 기존 필터링된 데이터를 가져오기
            List<Medicine> filteredMedicines = new ArrayList<>(medicinesPage.getContent());

            // ✅ 5. 국가 필터링 (출력 직전)
            if (!"ALL".equalsIgnoreCase(country)) {
                log.info("🔹 국가 필터링 적용 - 현재 국가: {}", country);
                List<Medicine> beforeFiltering = new ArrayList<>(filteredMedicines);

                filteredMedicines = beforeFiltering.stream()
                        .filter(medicine -> country.equalsIgnoreCase(medicine.getCountry()))
                        .collect(Collectors.toList());

                log.info("🔹 국가 필터링 전 개수: {}", beforeFiltering.size());
                log.info("🔹 국가 필터링 후 개수: {}", filteredMedicines.size());
            }

            // ✅ 6. 페이징 계산 (전체 개수 유지)
            int amountPage = (int) Math.ceil((double) totalCountBeforeFiltering / size); // 전체 페이지 수
            int currentCount = filteredMedicines.size(); // 현재 페이지의 개수

            // ✅ 7. 결과 변환
            List<MedicineResponseDTO> medicines = filteredMedicines.stream()
                    .map(medicine -> medicineConverter.convertFromEntity(medicine, userId))
                    .collect(Collectors.toList());

            // ✅ 8. 최종 로그 출력
            log.info("🔹 최종 반환 데이터 개수: {}", currentCount);
            filteredMedicines.forEach(medicine -> log.info("✅ 최종 결과 - 이름: {}, 국가: {}, 카테고리: {}",
                    medicine.getBrandName(), medicine.getCountry(), medicine.getCategory()));

            return new MedicineListPageResponseDTO(
                    totalCountBeforeFiltering, // 전체 개수 (국가 필터링 적용 전)
                    amountPage,  // 전체 페이지 수
                    currentCount, // 현재 페이지의 개수
                    page + 1, // 현재 페이지 번호
                    medicines);

        } catch (Exception e) {
            log.error("❌ DB에서 약물 데이터를 검색하는 중 오류 발생", e);
            throw new RuntimeException("DB에서 약물 데이터를 검색하는 중 오류 발생", e);
        }
    }





}

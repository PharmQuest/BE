package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyDetailsService;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MyPageServiceImpl implements MyPageService {

    private final PharmacyDetailsService pharmacyDetailsService;
    private final SupplementsScrapRepository supplementsScrapRepository;

    private final int PAGE_SIZE = 10;

    @Override
    public List<Supplements> getScrapSupplements(Long userId) {

        List<SupplementsScrap> supplementsScrapList = supplementsScrapRepository.findSupplementsByUserId(userId);
        if (supplementsScrapList.isEmpty()) {
            throw new NoSuchElementException("스크랩한 영양제가 없습니다.");
        }

        return supplementsScrapList.stream()
                .map(SupplementsScrap::getSupplements) // SupplementsScrap에서 supplements 값을 추출
                .toList();
    }

    @Override
    public Page<MyPageResponseDTO.PharmacyDto> getScrapPharmacies(User user, String country, Integer page) {

        // 스크랩된 전체 약국 placeId List
        List<String> pharmacyPlaceIdList = user.getPharmacyScraps();

        // 국가 분류
        String findingCountryName = PharmacyCountry.getCountryByName(country).getGoogleName(); // Query String으로 입력받은 국가의 google에 등록된 이름으로 변경

        // 찾는 국가의 약국만 필터링
        List<MyPageResponseDTO.PharmacyDto> pharmacyDtoList = pharmacyPlaceIdList.stream()
                .map(pharmacyDetailsService::getPharmacyDtoByPlaceId)
                .filter(pharmacyDto -> findingCountryName.equals(pharmacyDto.getCountry()) || findingCountryName.equals("all"))
                .toList();

        int totalElements = pharmacyDtoList.size();

        // 잘못된 페이지 요청 방지
        int totalPages = (int) Math.floor((double) totalElements / PAGE_SIZE);
        if (page < 1) {
            throw new CommonExceptionHandler(ErrorStatus.INVALID_PAGE_NUMBER);
        } else if (page > totalPages) {
            throw new CommonExceptionHandler(ErrorStatus.PAGE_NUMBER_EXCEEDS_TOTAL);
        }

        //페이징 처리
        Pageable pageable = PageRequest.of(page-1, PAGE_SIZE);
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalElements);

        return new PageImpl<>(pharmacyDtoList.subList(start, end), pageable, totalElements);
    }
}

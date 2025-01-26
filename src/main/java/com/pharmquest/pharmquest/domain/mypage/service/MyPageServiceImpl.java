package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyDetailsService;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<MyPageResponseDTO.PharmacyDto> getScrapPharmacies(User user, String country) {

        List<String> pharmacyPlaceIdList = user.getPharmacyScraps();
        String findingCountryName = PharmacyCountry.getCountryByName(country).getGoogleName(); // Query String으로 입력받은 국가의 google에 등록된 이름으로 변경

        return pharmacyPlaceIdList.stream()
                .map(pharmacyDetailsService::getPharmacyDtoByPlaceId)
                .filter(pharmacyDto -> findingCountryName.equals(pharmacyDto.getCountry()) || findingCountryName.equals("all"))
                .toList();

    }
}

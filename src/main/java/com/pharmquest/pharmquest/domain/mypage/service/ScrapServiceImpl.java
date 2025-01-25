package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.web.dto.ScrapResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyDetailsService;
import com.pharmquest.pharmquest.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final PharmacyDetailsService pharmacyDetailsService;

    @Override
    public List<ScrapResponseDTO.PharmacyDto> getPharmacies(User user, String country) {

        List<String> pharmacyPlaceIdList = user.getPharmacyScraps();
        String findingCountryName = PharmacyCountry.getCountryByName(country).getGoogleName(); // Query String으로 입력받은 국가의 google에 등록된 이름으로 변경

        return pharmacyPlaceIdList.stream()
                .map(pharmacyDetailsService::getPharmacyDtoByPlaceId)
                .filter(pharmacyDto -> findingCountryName.equals(pharmacyDto.getCountry()) || findingCountryName.equals("all"))
                .toList();

    }
}

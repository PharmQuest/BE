package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.medicine.service.TranslationService;
import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.pharmacy.web.dto.GooglePlaceDetailsResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class PharmacyDetailsServiceImpl implements PharmacyDetailsService {

    private final WebClient webClient;
    private final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/details/json";
    private final TranslationService translationService;

    @Value("${place.details.api-key}")
    private String API_KEY;

    public PharmacyDetailsServiceImpl(WebClient.Builder webClientBuilder, TranslationService translationService) {
        this.webClient = webClientBuilder.baseUrl(GOOGLE_PLACES_API_URL).build();
        this.translationService = translationService;
    }

    @Override
    public Boolean isPharmacyByPlaceId(String placeId) {

        GooglePlaceDetailsResponse response = getDetailsByPlaceId(placeId);
        if (response != null && "OK".equals(response.getStatus())) {
            return checkIfPharmacy(response);
        }
        else{
            throw new CommonExceptionHandler(ErrorStatus.PHARMACY_REQUEST_FAILED);
        }
    }

    // placeId로 상세정보를 불러와 Dto로 변환
    @Override
    public MyPageResponseDTO.PharmacyDto getPharmacyDtoByPlaceId(String placeId) {

        GooglePlaceDetailsResponse response = getDetailsByPlaceId(placeId);
        GooglePlaceDetailsResponse.Result detailsResult = response.getResult();

        return MyPageResponseDTO.PharmacyDto.builder()
                .name(detailsResult.getName())
                .placeId(placeId)
                .openNow(detailsResult.getOpeningHours().getOpenNow())
                .region(getTranslatedLocation(response, detailsResult.getLocationList()))
                .latitude(detailsResult.getGeometry().getLocation().getLat())
                .longitude(detailsResult.getGeometry().getLocation().getLng())
                .country(getCountryName(response))
                .periods(detailsResult.getOpeningHours().getPeriods())
                .imgUrl("imgURL")
                .build();
    }

    // 장소 번역
    private String getTranslatedLocation(GooglePlaceDetailsResponse response, List<String> locationList) {

        String targetLanguage = PharmacyCountry.getLanguage(getCountryName(response));

        List<String> translatedLocation = new ArrayList<>(locationList.stream()
                .map(location -> {
                    try {
                        return translationService.translateText(location, targetLanguage).trim();
                    } catch (Exception e) {
                        log.error("Translation failed: {}", e.getMessage());
                        return location.trim();
                    }
                }).toList());

        if ("ko".equals(targetLanguage)) {
            Collections.reverse(translatedLocation);
        }

        // ex) ["Beverly Hills", "Los Angeles County", "California"] -> "Beverly Hills Los Angeles County California"
        return String.join(" ", translatedLocation);
    }

    // 상세정보로부터 국가 이름 가져옴
    private String getCountryName(GooglePlaceDetailsResponse response) {
        if (response.getResult().getAddressComponents() != null) {
            return response.getResult().getAddressComponents().stream()
                    .filter(component -> component.getTypes().contains("country"))
                    .map(GooglePlaceDetailsResponse.AddressComponent::getLongName)
                    .findFirst()
                    .orElse("Unknown");
        }
        return "Unknown";
    }

    // placeId를 가지고 api로 상세정보 조회
    private GooglePlaceDetailsResponse getDetailsByPlaceId(String placeId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", API_KEY)
                        .queryParam("place_id", placeId)
                        .build())
                .retrieve()
                .bodyToMono(GooglePlaceDetailsResponse.class)
                .block();
    }

    // api로부터 불러온 정보를 바탕으로 약국인지 확인
    private Boolean checkIfPharmacy(GooglePlaceDetailsResponse response) {

        if (response.getResult() != null && response.getResult().getTypes() != null) {
            List<String> types = response.getResult().getTypes();
            log.info("약국 타입 List = {}",types.toString());
            return types.contains("pharmacy") || types.contains("drugstore") || types.contains("hospital") || types.contains("health"); // 약국 중에 장소 타입이 pharmacy 아니고 drugstore로 적혀있는 곳도 있음
        }else if(response.getResult() == null){
            throw new CommonExceptionHandler(ErrorStatus.PLACE_NO_RESULT);
        }
        return false;
    }

}

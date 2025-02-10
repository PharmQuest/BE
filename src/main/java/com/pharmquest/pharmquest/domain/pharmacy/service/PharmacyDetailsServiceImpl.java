package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.medicine.service.TranslationService;
import com.pharmquest.pharmquest.domain.pharmacy.ImageUtil;
import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.pharmacy.web.dto.GooglePlaceDetailsResponse;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
public class PharmacyDetailsServiceImpl implements PharmacyDetailsService {

    private final WebClient webClient;
    private final ImageUtil imageUtil;
    private final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/details/json";
    private final TranslationService translationService;

    @Value("${place.details.api-key}")
    private String API_KEY;

    public PharmacyDetailsServiceImpl(WebClient.Builder webClientBuilder, TranslationService translationService, ImageUtil imageUtil) {
        this.webClient = webClientBuilder.baseUrl(GOOGLE_PLACES_API_URL).build();
        this.translationService = translationService;
        this.imageUtil = imageUtil;
    }

    @Override
    public Boolean isPharmacyByPlaceId(String placeId) {

        GooglePlaceDetailsResponse response = getDetailsByPlaceId(placeId);
        if ("OK".equals(response.getStatus())) {
            return checkIfPharmacy(response);
        }
        // 구글에서 가져온 응답의 status가 OK 아니면 그냥 약국 아닌걸로
        return false;
    }

    // placeId로 Pharmacy 객체 반환
    @Override
    public Pharmacy getPharmacyByPlaceId(String placeId) {
        GooglePlaceDetailsResponse response = getDetailsByPlaceId(placeId);
        GooglePlaceDetailsResponse.Result detailsResult = response.getResult();
        return Pharmacy.builder()
                .name(detailsResult.getName())
                .placeId(placeId)
                .region(getTranslatedLocation(response, detailsResult.getLocationList()))
                .latitude(detailsResult.getGeometry().getLocation().getLat())
                .longitude(detailsResult.getGeometry().getLocation().getLng())
                .country(PharmacyCountry.getCountryByGoogleName(getCountryName(response)))
                .build();
    }

    @Override
    public String getImgURLByPlaceId(String placeId) {
        GooglePlaceDetailsResponse response = getDetailsByPlaceId(placeId);
        return imageUtil.getPharmacyImageURL(response);
    }

    // 장소 번역
    private String getTranslatedLocation(GooglePlaceDetailsResponse response, List<String> locationList) {

        String targetLanguage = PharmacyCountry.getLanguage(getCountryName(response));
        if (locationList.isEmpty()) {
            return "주소 미제공";
        }
        String location = String.join(" ", locationList);
        try {
            return translationService.translateText(location, targetLanguage);
        } catch (Exception e) {
            log.error("Translation failed: {}", e.getMessage());
            return location.trim();
        }
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

        if (response != null && response.getResult() != null && response.getResult().getTypes() != null) {
            List<String> types = response.getResult().getTypes();
            return types.contains("pharmacy") || types.contains("drugstore") || types.contains("hospital") || types.contains("health"); // 약국 중에 장소 타입이 pharmacy 아니고 drugstore로 적혀있는 곳도 있음
        }else{
            throw new CommonExceptionHandler(ErrorStatus.PLACE_NO_INFO);
        }

    }

}

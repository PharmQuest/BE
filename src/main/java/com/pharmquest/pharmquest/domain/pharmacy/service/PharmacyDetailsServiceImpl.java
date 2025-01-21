package com.pharmquest.pharmquest.domain.pharmacy.service;

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
    private final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/details/json";

    @Value("${place.details.api-key}")
    private String API_KEY;

    public PharmacyDetailsServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(GOOGLE_PLACES_API_URL).build();
    }

    @Override
    public Boolean isPharmacyByPlaceId(String placeId) {

        GooglePlaceDetailsResponse response;
        response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", API_KEY)
                        .queryParam("place_id", placeId)
                        .build())
                .retrieve()
                .bodyToMono(GooglePlaceDetailsResponse.class)
                .block();

        if (response != null && "OK".equals(response.getStatus())) {
            return checkIfPharmacy(response);
        }
        else{
            throw new CommonExceptionHandler(ErrorStatus.PHARMACY_REQUEST_FAILED);
        }
    }


    private Boolean checkIfPharmacy(GooglePlaceDetailsResponse response) {

        if (response.getResult() != null && response.getResult().getTypes() != null) {
            List<String> types = response.getResult().getTypes();
            return types.contains("pharmacy") || types.contains("drugstore"); // 약국 중에 장소 타입이 pharmacy 아니고 drugstore로 적혀있는 곳도 있음
        }else if(response.getResult() == null){
            throw new CommonExceptionHandler(ErrorStatus.PLACE_NO_RESULT);
        }
        return false;
    }

}

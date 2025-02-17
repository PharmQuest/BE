package com.pharmquest.pharmquest.domain.pharmacy;

import com.pharmquest.pharmquest.domain.pharmacy.web.dto.GooglePlaceDetailsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.List;

@Component
public class ImageUtil {

    @Value("${place.details.api-key}")
    private String API_KEY;

    private final String GOOGLE_PLACE_IMG_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private final int IMG_MAX_SIZE = 200;

    // photo_reference과 api key 이용하여 이미지 요청 후 -> Base64 인코딩된 url 반환
    public String getPharmacyImageURL(String photoReference) {

        String url = GOOGLE_PLACE_IMG_URL + "maxwidth=" + IMG_MAX_SIZE + "&maxheight" + IMG_MAX_SIZE + "&photo_reference=" + photoReference + "&key=" + API_KEY;
        try {
            return encodeToBase64(url);
        }catch (Exception e) { // 문제 있으면 기본 사진 반환
            return "https://umc-pharmquest.s3.ap-northeast-2.amazonaws.com/d09fa082-76d2-4c17-ad0a-e4800814ec61_pharm_default_img_1.jpg";
        }
    }

    private String encodeToBase64(String url) throws IOException {
        byte[] imageBytes = downloadImage(url);
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    private byte[] downloadImage(String imageUrl) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream()) {
            return in.readAllBytes();
        }
    }

}

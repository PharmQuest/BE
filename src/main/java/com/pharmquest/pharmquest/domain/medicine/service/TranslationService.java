package com.pharmquest.pharmquest.domain.medicine.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class TranslationService {

    private final Translate translate;

    public TranslationService(@Value("${google.cloud.json-key}") String base64JsonKey) throws IOException {
        GoogleCredentials credentials;

        if (new File(base64JsonKey).exists()) {
            // 로컬 환경: 파일 경로 사용
            credentials = GoogleCredentials.fromStream(new FileInputStream(base64JsonKey));
        } else {
            // CI/CD 환경: Base64 값 디코딩
            byte[] decodedJson = Base64.getDecoder().decode(base64JsonKey);
            credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(decodedJson));
        }

        this.translate = TranslateOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public String translateText(String text, String targetLanguage) {
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage)
        );
        return translation.getTranslatedText();
    }
}
package com.pharmquest.pharmquest.domain.medicine.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class TranslationService {

    private final Translate translate;

    public TranslationService() throws IOException {
        // JSON 키 파일 경로를 지정
        String jsonKeyFilePath = "src/main/resources/skilful-card-445716-m2-6364abe85b4b.json";
        // Google Cloud Translation API 클라이언트 초기화
        this.translate = TranslateOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(jsonKeyFilePath)))
                .build()
                .getService();
    }

    public String translateText(String text, String targetLanguage) {
        // 텍스트 번역
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage)
        );
        return translation.getTranslatedText();
    }
}

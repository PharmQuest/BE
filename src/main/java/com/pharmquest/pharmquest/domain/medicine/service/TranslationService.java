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

    public TranslationService(@Value("${google.cloud.json-key-path}") String jsonKeyPath) throws IOException {
        GoogleCredentials credentials;

        File jsonFile = new File(jsonKeyPath);
        if (jsonFile.exists()) {
            // 로컬 환경: 파일 경로로 GoogleCredentials 생성
            credentials = GoogleCredentials.fromStream(new FileInputStream(jsonFile));
        } else {
            // 배포 환경: GOOGLE_APPLICATION_CREDENTIALS 환경 변수 사용
            String envKeyPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            if (envKeyPath != null && new File(envKeyPath).exists()) {
                credentials = GoogleCredentials.fromStream(new FileInputStream(envKeyPath));
            } else {
                throw new IllegalArgumentException("Google Cloud JSON Key를 찾을 수 없습니다!");
            }
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
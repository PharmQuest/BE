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
import java.nio.charset.StandardCharsets;

@Service
public class TranslationService {

    private final Translate translate;

    // YAML 파일에서 경로를 가져오기 위해 @Value 어노테이션 사용
    public TranslationService(@Value("${google.cloud.json-key-path}") String jsonKeyPath) throws IOException {
        GoogleCredentials credentials;

        if (new File(jsonKeyPath).exists()) {
            // 로컬 환경: 파일 경로에서 GoogleCredentials 생성
            credentials = GoogleCredentials.fromStream(new FileInputStream(jsonKeyPath));
        } else {
            // 배포 환경: JSON 값 자체를 환경 변수 또는 application.yml에서 가져옴
            credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(jsonKeyPath.getBytes(StandardCharsets.UTF_8))
            );
        }

        this.translate = TranslateOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    // 주어진 텍스트를 대상 언어로 번역

    public String translateText(String text, String targetLanguage) {
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage)
        );
        return translation.getTranslatedText();
    }
}
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

    public TranslationService(@Value("${google.cloud.json}") String jsonKey) throws IOException {
        if (jsonKey == null || jsonKey.isEmpty()) {
            throw new IllegalArgumentException("Google Cloud JSON Key가 비어 있습니다!");
        }

        // JSON 문자열로 GoogleCredentials 생성
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(jsonKey.getBytes())
        );

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
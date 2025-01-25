package com.pharmquest.pharmquest.domain.medicine.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

        // JSON 유효성 검증
        validateJson(jsonKey);

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
        return this.translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage)
        ).getTranslatedText();
    }

    /**
     * JSON 유효성을 검증하는 메서드
     *
     * @param jsonKey JSON 문자열
     */
    private void validateJson(String jsonKey) {
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonKey);
            if (!jsonElement.isJsonObject()) {
                throw new IllegalArgumentException("JSON Key가 유효한 객체 형식이 아닙니다!");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON Key가 유효하지 않습니다!", e);
        }
    }
}
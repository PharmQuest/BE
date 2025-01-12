package com.pharmquest.pharmquest.domain.home;

import com.pharmquest.pharmquest.common.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.common.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostCategory {

    FORUM("포럼"),
    PHARMACY("약국"),
    HOSPITAL("병원"),
    MEDICATION("약"),
    SYMPTOM("증상"),
    SUPPLEMENT("영양제");

    private final String koreanName;

    PostCategory(String name) {
        this.koreanName = name;
    }

    public static PostCategory fromKoreanName(String koreanName) {
        return Arrays.stream(values())
                .filter(category -> category.getKoreanName().equals(koreanName))
                .findFirst()
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.ILLEGAL_POST_CATEGORY));
    }


}


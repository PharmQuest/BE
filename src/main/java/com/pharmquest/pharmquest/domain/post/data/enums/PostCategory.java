package com.pharmquest.pharmquest.domain.post.data.enums;


import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostCategory {

    FORUM("자유"),
    PHARMACY("약국"),
    HOSPITAL("병원"),
    MEDICATION("약"),
    SYMPTOM("증상"),
    SUPPLEMENT("영양제"),
    ALL("전체");

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
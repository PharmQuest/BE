package com.pharmquest.pharmquest.domain.post.data.enums;

import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReportType {

    SPAM("스팸 홍보/도배"),
    OBSCENE_CONTENT("음란물"),
    ILLEGAL_INFO("불법 정보 포함"),
    PROFANITY("욕설"),
    HATE_SPEECH("비하/혐오/차별적 표현"),
    FRAUD("유출/사칭/사기"),
    ILLEGAL_RECORDINGS("불법 촬영물 유통"),
    ADS("상업적 광고 및 판매");

    private final String koreanName;

    ReportType(String name) {
        this.koreanName = name;
    }

    public static ReportType fromKoreanName(String koreanName) {
        return Arrays.stream(values())
                .filter(type -> type.getKoreanName().equals(koreanName))
                .findFirst()
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.ILLEGAL_POST_CATEGORY));
    }

    }

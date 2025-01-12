package com.pharmquest.pharmquest.domain.home;

import com.pharmquest.pharmquest.common.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.common.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Country {

    USA("미국", "US"),
    JAPAN("일본", "JP"),
    CHINA("중국", "CH")
    ;

    private final String koreanName;
    private final String shortName;

    Country(String koreanName, String shortName) {
        this.koreanName = koreanName;
        this.shortName = shortName;
    }

    public static Country fromKoreanName(String koreanName) {
        return Arrays.stream(values())
                .filter(country -> country.getKoreanName().equals(koreanName))
                .findFirst()
                .orElseThrow(() -> new CommonExceptionHandler(ErrorStatus.ILLEGAL_COUNTRY));
    }

}

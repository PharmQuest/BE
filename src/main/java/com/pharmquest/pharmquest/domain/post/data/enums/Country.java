package com.pharmquest.pharmquest.domain.post.data.enums;

import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Country {

    KOREA("대한민국", "KR"),
    USA("미국", "US"),
    JAPAN("일본", "JP"),
    CHINA("중국", "CH"),
    CANADA("캐나다", "CA"),
    AUSTRALIA("호주", "AU"),
    VIETNAM("베트남", "VN"),
    THAILAND("태국", "TH"),
    PHILIPPINES("필리핀", "PH"),
    SINGAPORE("싱가포르", "SG"),
    EUROPE("유럽", "EU"),
    NONE("선택없음", "NONE");

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

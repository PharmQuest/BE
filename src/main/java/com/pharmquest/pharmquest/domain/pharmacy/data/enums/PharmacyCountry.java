package com.pharmquest.pharmquest.domain.pharmacy.data.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PharmacyCountry {

    KOREA("South Korea","korea", "ko"),
    USA("United States", "usa", "en"),
    ALL("all",  "all", "en"),
    ;

    private final String googleName; // 구글에 등록된 국가 이름 형식
    private final String paramName; // query string으로 받을 국가 이름 형식
    private final String lang;

    PharmacyCountry(String googleName, String paramName, String lang) {
        this.googleName = googleName;
        this.paramName = paramName;
        this.lang = lang;
    }

    // query string 으로 받은 국가 이름을 PharmacyCountry로 반환. 해당하는거 없을 경우 '전체' 반환
    public static PharmacyCountry getCountryByName(String name) {
        return Arrays.stream(values())
                .filter(country -> name.equals(country.paramName))
                .findFirst()
                .orElse(ALL);
    }

    public static String getLanguage(String googleName) {
        PharmacyCountry pharmacyCountry = Arrays.stream(values())
                .filter(country -> googleName.equals(country.googleName))
                .findFirst()
                .orElse(ALL);
        return pharmacyCountry.lang;
    }

}

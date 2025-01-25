package com.pharmquest.pharmquest.domain.pharmacy.data.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PharmacyCountry {

    KOREA("South Korea", "korea"),
    USA("United States", "usa"),
    ALL("all", "all")
    ;

    private final String googleName; // 구글에 등록된 국가 이름 형식
    private final String paramName; // query string으로 받을 국가 이름 형식

    PharmacyCountry(String googleName, String paramName) {
        this.googleName = googleName;
        this.paramName = paramName;
    }

    // query string 으로 받은 국가 이름을 PharmacyCountry로 반환. 해당하는거 없을 경우 '전체' 반환
    public static PharmacyCountry getCountryByName(String name) {
        return Arrays.stream(values())
                .filter(country -> name.equals(country.paramName))
                .findFirst()
                .orElse(ALL);
    }

}

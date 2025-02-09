package com.pharmquest.pharmquest.domain.supplements.data.Enum;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum CategoryGroup {
    전체,
    면역("항산화", "기관지", "미네랄"),
    피로("피로", "간건강", "체력"),
    소화("속쓰림", "소화", "유산균", "장건강"),
    피부("피부", "두피", "발진", "자외선"),
    관절("관절", "허리", "염좌", "칼슘"),
    눈건강("눈건강", "루테인");

    private final List<String> categories;

    CategoryGroup(String... categories) {
        this.categories = Arrays.asList(categories);
    }
}

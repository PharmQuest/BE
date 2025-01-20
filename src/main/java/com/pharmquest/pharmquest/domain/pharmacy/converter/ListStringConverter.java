package com.pharmquest.pharmquest.domain.pharmacy.converter;

import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.List;

public class ListStringConverter implements AttributeConverter<List<String>, String> {

    @Override // DB에 저장하는거 placeId -> db 문자열에 포함
    public String convertToDatabaseColumn(List<String> placeIdList) {
        // 문자열을 합칠 때마다 객체를 만드는 것을 막기 위해 StringBuilder 사용
        StringBuilder sb = new StringBuilder();
        placeIdList // List에 담긴 String들 모두 합침
                .forEach(placeId -> sb.append(",").append(placeId));
        return sb.toString();
    }

    @Override // DB 문자열 -> placeId 목록 : 약국 조회 때 쓰임
    public List<String> convertToEntityAttribute(String placeIdString) {
        return new ArrayList<>(List.of(","));
    }

}

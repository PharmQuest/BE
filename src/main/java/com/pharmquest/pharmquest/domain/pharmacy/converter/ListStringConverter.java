package com.pharmquest.pharmquest.domain.pharmacy.converter;

import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.List;

public class ListStringConverter implements AttributeConverter<List<String>, String> {

    @Override // DB에 저장하는거 placeId -> db 문자열에 포함
    public String convertToDatabaseColumn(List<String> placeIdList) {
        if (placeIdList == null || placeIdList.isEmpty()) {
            return "";
        }
        return String.join(",", placeIdList);
    }

    @Override // DB 문자열 -> placeId 목록 : 약국 조회 때 쓰임
    public List<String> convertToEntityAttribute(String placeIdString) {
        if (placeIdString == null || placeIdString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(placeIdString.split(","));
    }

}

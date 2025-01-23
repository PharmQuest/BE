package com.pharmquest.pharmquest.domain.home.converter;

import com.pharmquest.pharmquest.domain.home.web.dto.HomePostResponseDTO;
import com.pharmquest.pharmquest.domain.post.data.Post;

import java.time.LocalDateTime;
import java.util.List;

public class HomePostConverter {

    public static List<HomePostResponseDTO> postListToDtoList(List<Post> postList) {
        return postList.stream()
                .map(HomePostConverter::postToDto)
                .toList();
    }

    public static HomePostResponseDTO postToDto(Post post) {
        return HomePostResponseDTO.builder()
                .title(post.getTitle())
                .createdAt(convertDate(post.getCreatedAt()))
                .category(post.getCategory().getKoreanName())
                .build();
    }

    // 2025-01-10T12:00:00 -> 2025.01.10 변환
    private static String convertDate(LocalDateTime dateTime) {

        int indexOfT = dateTime.toString().indexOf("T"); // T 이전 값 추출 위해 T의 index 저장
        return dateTime.toString().
                substring(0, indexOfT).
                replaceAll("-", ".");
    }

}

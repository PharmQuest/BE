package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.converter.MyPageConverter;
import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyDetailsService;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.repository.scrap.PostScrapRepository;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommonExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MyPageServiceImpl implements MyPageService {
    private final MyPageConverter myPageConverter;
    private final PharmacyDetailsService pharmacyDetailsService;
    private final SupplementsScrapRepository supplementsScrapRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;

    private final int PAGE_SIZE = 10;

    @Override
    public Page<MyPageResponseDTO.SupplementsResponseDto> getScrapSupplements(Long userId, Pageable pageable, CategoryKeyword category) {


        Page<SupplementsScrap> supplementsScrapPage = supplementsScrapRepository.findSupplementsByUserId(userId, pageable);

        List<MyPageResponseDTO.SupplementsResponseDto> supplementsDtos = supplementsScrapPage.stream()
                .map(supplementsScrap -> myPageConverter.toSupplementsDto(supplementsScrap.getSupplements(), category))
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(supplementsDtos, pageable, supplementsScrapPage.getTotalElements());

    }

    @Override
    public Page<MyPageResponseDTO.ScrapPostResponseDTO> getScrapPosts(Long userId, Pageable pageable) {

        Page<PostScrap> postScrapPage = postScrapRepository.findPostByUserId(userId, pageable);

        if (postScrapPage.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, postScrapPage.getTotalElements());
        }

        List<MyPageResponseDTO.ScrapPostResponseDTO> scrapedPostDTO = postScrapPage.stream()
                .map(postScrap -> myPageConverter.toScrapedPostDto(postScrap.getPost()))
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(scrapedPostDTO, pageable, postScrapPage.getTotalPages());
    }

    @Override
    public Page<MyPageResponseDTO.PostResponseDTO> getMyPosts(Long userId, Pageable pageable) {
        Page<Post> postPage = postRepository.findPostByUserId(userId, pageable);

        if (postPage.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, postPage.getTotalElements());
        } else {

            List<MyPageResponseDTO.PostResponseDTO> PostDTO = postPage.stream()
                    .map(myPageConverter::toPostDto)
                    .filter(Objects::nonNull)
                    .toList();

            return new PageImpl<>(PostDTO, pageable, postPage.getTotalPages());
        }
    }

    @Override
    public Page<MyPageResponseDTO.PharmacyDto> getScrapPharmacies(User user, String country, Integer page) {

        // 스크랩된 전체 약국 placeId List
        List<String> pharmacyPlaceIdList = user.getPharmacyScraps();

        // 국가 분류
        String findingCountryName = PharmacyCountry.getCountryByName(country).getGoogleName(); // Query String으로 입력받은 국가의 google에 등록된 이름으로 변경

        // 찾는 국가의 약국만 필터링
        List<MyPageResponseDTO.PharmacyDto> pharmacyDtoList = pharmacyPlaceIdList.stream()
                .map(pharmacyDetailsService::getPharmacyDtoByPlaceId)
                .filter(pharmacyDto -> findingCountryName.equals(pharmacyDto.getCountry()) || findingCountryName.equals("all"))
                .toList();

        int totalElements = pharmacyDtoList.size();

        // 잘못된 페이지 요청 방지
        int totalPages = (int) Math.floor((double) totalElements / PAGE_SIZE);
        if (page < 1) { // 1보다 작은 경우
            throw new CommonExceptionHandler(ErrorStatus.INVALID_PAGE_NUMBER);
        } else if (page-1 > totalPages) { // 페이지 수 초과
            throw new CommonExceptionHandler(ErrorStatus.PAGE_NUMBER_EXCEEDS_TOTAL);
        }

        //페이징 처리
        Pageable pageable = PageRequest.of(page-1, PAGE_SIZE);
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalElements);

        return new PageImpl<>(pharmacyDtoList.subList(start, end), pageable, totalElements);
    }

}

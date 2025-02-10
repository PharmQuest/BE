package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.medicine.repository.MedicineScrapRepository;
import com.pharmquest.pharmquest.domain.mypage.converter.MyPageConverter;
import com.pharmquest.pharmquest.domain.mypage.data.MedicineScrap;
import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import com.pharmquest.pharmquest.domain.mypage.web.dto.MyPageResponseDTO;
import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.domain.pharmacy.service.PharmacyDetailsService;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.repository.comment.PostCommentRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.repository.scrap.PostScrapRepository;
import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryGroup;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsCategoryRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
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

import java.util.*;
import java.util.stream.Collectors;

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
    private final PostCommentRepository postCommentRepository;
    private final MedicineScrapRepository medicineScrapRepository;
    private final SupplementsRepository supplementsRepository;
    private final SupplementsCategoryRepository supplementsCategoryRepository;

    @Override
    public Page<MyPageResponseDTO.SupplementsResponseDto> getScrapSupplements(
            Long userId,
            Pageable pageable,
            CategoryGroup categoryGroup) {

        Page<Supplements> supplementsPage;
        List<Long> scrapAllSupplementsIds = supplementsScrapRepository.findSupplementsIdByUserId(userId);
        if (categoryGroup == null || categoryGroup == CategoryGroup.전체) {
            supplementsPage = supplementsRepository.findByIdIn(scrapAllSupplementsIds, pageable);
        } else {
            List<Long> scrapFilteredSupplementsIds = supplementsCategoryRepository.findSupplementIdByCategoryNameAndIds(categoryGroup.getCategories(),scrapAllSupplementsIds);
            supplementsPage = supplementsRepository.findByIdIn(scrapFilteredSupplementsIds, pageable);
        }

        if(supplementsPage.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, supplementsPage.getTotalElements());
        }

        List<MyPageResponseDTO.SupplementsResponseDto> supplementDTO = supplementsPage.stream()
                .map(myPageConverter::toSupplementsDto)
                .toList();

        return new PageImpl<>(supplementDTO, pageable, supplementsPage.getTotalElements());
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
    public Page<MyPageResponseDTO.CommentResponseDTO> getMyComments(Long userId, Pageable pageable) {
        Page<Comment> commentPage = postCommentRepository.findCommentByUserId(userId, pageable);

        if (commentPage.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, commentPage.getTotalElements());
        } else {
            List<MyPageResponseDTO.CommentResponseDTO> CommentDTO = commentPage.stream()
                    .map(myPageConverter::toCommentDto)
                    .filter(Objects::nonNull)
                    .toList();

            return new PageImpl<>(CommentDTO, pageable, commentPage.getTotalPages());
        }
    }

    @Override
    public Page<MyPageResponseDTO.MedicineResponseDto> getScrapMedicines(Long userId, Pageable pageable, String requestCountry) {

        String country = "전체".equals(requestCountry) ? null : requestCountry;
        Page<MedicineScrap> medicinePage = medicineScrapRepository.findMedicineByUserIdAndCountry(userId, country, pageable);

        if(medicinePage.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, medicinePage.getTotalElements());
        }

        List<MyPageResponseDTO.MedicineResponseDto> medicineDto = medicinePage.stream()
                .map(supplementsScrap -> myPageConverter.toMedicineDto(supplementsScrap.getMedicine()))
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(medicineDto, pageable, medicinePage.getTotalElements());
    }

    @Override
    public Page<MyPageResponseDTO.notificationResponseDTO> getNotification(Long userId, Pageable pageable) {
        Page<Comment> notificationCommentPage = postCommentRepository.findUserRelatedComments(userId, pageable);

        if (notificationCommentPage.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, notificationCommentPage.getTotalElements());
        } else{
            List<MyPageResponseDTO.notificationResponseDTO> notificationCommentDTO = notificationCommentPage.stream()
                    .map(myPageConverter::toNotificationCommentDTO)
                    .filter(Objects::nonNull)
                    .toList();
            return new PageImpl<>(notificationCommentDTO, pageable, notificationCommentPage.getTotalPages());
        }
    }

    @Override
    public Page<MyPageResponseDTO.PharmacyDto> getScrapPharmacies(User user, PharmacyCountry country, Integer page, Integer size) {
        // 스크랩된 전체 약국 placeId List
        List<String> pharmacyPlaceIdList = user.getPharmacyScraps();

        // 찾는 국가의 약국만 필터링
        List<MyPageResponseDTO.PharmacyDto> pharmacyDtoList = pharmacyPlaceIdList.stream()
                .map(pharmacyDetailsService::getPharmacyDtoByPlaceId)
                .filter(
                        pharmacyDto -> country.equals(PharmacyCountry.getCountryByGoogleName(pharmacyDto.getCountry()))
                                    || country.equals(PharmacyCountry.ALL)
                )
                .toList();

        int totalElements = pharmacyDtoList.size();

        if (size < 1) { // 사이즈 검증
            throw new CommonExceptionHandler(ErrorStatus.INVALID_SIZE_NUMBER);
        }

        // 페이지 검증
        int totalPages = (int) Math.floor((double) totalElements / size);
        if (page < 1) { // 1보다 작은 경우
            throw new CommonExceptionHandler(ErrorStatus.INVALID_PAGE_NUMBER);
        } else if (page-1 > totalPages) { // 페이지 수 초과
            throw new CommonExceptionHandler(ErrorStatus.PAGE_NUMBER_EXCEEDS_TOTAL);
        }

        //페이징 처리
        Pageable pageable = PageRequest.of(page-1, size);
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalElements);
        return new PageImpl<>(pharmacyDtoList.subList(start, end), pageable, totalElements);
    }

}

package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.mypage.converter.MyPageConverter;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final SupplementsScrapRepository supplementsScrapRepository;
    @Override
    public List<Supplements> getScrapSupplements(Long userId) {

        List<SupplementsScrap> supplementsScrapList = supplementsScrapRepository.findSupplementsByUserId(userId);
        if (supplementsScrapList.isEmpty()) {
            throw new NoSuchElementException("스크랩한 영양제가 없습니다.");
        }

        return supplementsScrapList.stream()
                .map(SupplementsScrap::getSupplements) // SupplementsScrap에서 supplements 값을 추출
                .toList();
    }
}

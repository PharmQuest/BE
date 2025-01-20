package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsRepository;
import com.pharmquest.pharmquest.domain.supplements.repository.SupplementsScrapRepository;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsScrapResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class SupplementsScrapServiceImpl implements SupplementsScrapService{
    private final SupplementsRepository supplementsRepository;
    private final UserRepository userRepository;
    private final SupplementsScrapRepository supplementsScrapRepository;

    @Override
    @Transactional
    public SupplementsScrapResponseDTO changeScrap(Long supplementsId, Long userId) {
        Supplements supplement = supplementsRepository.findById(supplementsId).orElse(null);

        User user = userRepository.findById(userId).orElse(null);

        Optional<SupplementsScrap> existingScrap = supplementsScrapRepository.findByUserAndSupplements(user, supplement);
        boolean isScrapped;

        if (existingScrap.isPresent()) {
            supplementsScrapRepository.delete(existingScrap.get());
            supplement.setScrapCount(supplement.getScrapCount() - 1);
            isScrapped = false;
        } else {
            SupplementsScrap scrap = new SupplementsScrap();
            scrap.setUser(user);
            scrap.setSupplements(supplement);
            scrap.setIsSuccess(true);
            supplementsScrapRepository.save(scrap);
            supplement.setScrapCount(supplement.getScrapCount() + 1);
            isScrapped = true;
        }

        supplementsRepository.save(supplement);

        return SupplementsScrapResponseDTO.builder()
                .supplementId(supplement.getId())
                .scrapCount(supplement.getScrapCount())
                .message(isScrapped ? "스크랩 되었습니다." : "스크랩이 취소되었습니다.")
                .build();
    }
}

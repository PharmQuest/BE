package com.pharmquest.pharmquest.domain.pharmacy.service;

import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PharmacyCommandServiceImpl implements PharmacyCommandService {

    private final UserRepository userRepository;

    @Override
    public Boolean scrapPharmacy(Long userId, Long pharmacyId) {

        boolean userExist = userRepository.existsById(userId);

        return null;
    }
}

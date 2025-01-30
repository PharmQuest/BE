package com.pharmquest.pharmquest.domain.medicine.service;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineScrapRepository;
import com.pharmquest.pharmquest.domain.mypage.data.MedicineScrap;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class MedicineScrapService {

    private final MedicineScrapRepository scrapRepository;
    private final MedRepository medicineRepository;
    private final JwtUtil jwtUtil;

    /* 스크랩 추가 */
    @Transactional
    public void addScrap(String authorizationHeader, Long medicineId) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);  // 🔥 JWT에서 userId 가져오기
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약물을 찾을 수 없습니다."));

        // 이미 스크랩했는지 확인
        if (scrapRepository.existsByUserAndMedicine(user, medicine)) {
            throw new IllegalStateException("이미 스크랩한 약물입니다.");
        }

        MedicineScrap scrap = new MedicineScrap();
        scrap.setUser(user);
        scrap.setMedicine(medicine);

        scrapRepository.save(scrap);
    }

    /* 스크랩 목록 조회 */
    @Transactional(readOnly = true)
    public List<Medicine> getScrappedMedicines(String authorizationHeader) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);  // 🔥 JWT에서 userId 가져오기
        List<MedicineScrap> scraps = scrapRepository.findByUser(user);

        return scraps.stream()
                .map(MedicineScrap::getMedicine)
                .collect(Collectors.toList());
    }

    /* 스크랩 삭제 */
    @Transactional
    public void removeScrap(String authorizationHeader, Long medicineId) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);  // 🔥 JWT에서 userId 가져오기
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약물을 찾을 수 없습니다."));

        MedicineScrap scrap = scrapRepository.findByUserAndMedicine(user, medicine)
                .orElseThrow(() -> new IllegalArgumentException("스크랩한 적 없는 약물입니다."));

        scrapRepository.delete(scrap);
    }
}
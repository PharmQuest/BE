package com.pharmquest.pharmquest.domain.medicine.service;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineScrapRepository;
import com.pharmquest.pharmquest.domain.mypage.data.MedicineScrap;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class MedicineScrapService {
    private final MedicineScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final MedRepository medRepository;

    public MedicineScrapService(MedicineScrapRepository scrapRepository, UserRepository userRepository, MedRepository medRepository) {
        this.scrapRepository = scrapRepository;
        this.userRepository = userRepository;
        this.medRepository = medRepository;
    }

    /** ✅ 1. 약물 스크랩 추가 */
    @Transactional
    public void addScrap(Long userId, Long medicineId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 사용자가 없습니다: " + userId));

        Medicine medicine = medRepository.findById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 약물이 없습니다: " + medicineId));

        // 이미 스크랩한 경우 예외 처리
        if (scrapRepository.findByUserIdAndMedicineId(userId, medicineId).isPresent()) {
            throw new IllegalStateException("이미 스크랩한 약물입니다.");
        }

        MedicineScrap scrap = new MedicineScrap(user, medicine);
        scrapRepository.save(scrap);
    }

    /** ✅ 2. 특정 사용자의 스크랩 목록 조회 */
    public List<Medicine> getScrappedMedicines(Long userId) {
        return scrapRepository.findByUserId(userId).stream()
                .map(MedicineScrap::getMedicine)
                .collect(Collectors.toList());
    }

    /** ✅ 3. 스크랩 삭제 */
    @Transactional
    public void removeScrap(Long userId, Long medicineId) {
        if (!scrapRepository.findByUserIdAndMedicineId(userId, medicineId).isPresent()) {
            throw new IllegalStateException("스크랩하지 않은 약물입니다.");
        }
        scrapRepository.deleteByUserIdAndMedicineId(userId, medicineId);
    }
}

package com.pharmquest.pharmquest.domain.medicine.repository;

import com.pharmquest.pharmquest.domain.mypage.data.MedicineScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineScrapRepository extends JpaRepository<MedicineScrap, Long> {
    // 특정 유저가 특정 약물을 스크랩했는지 확인
    Optional<MedicineScrap> findByUserIdAndMedicineId(Long userId, Long medicineId);

    // 특정 유저가 스크랩한 모든 약물 조회
    List<MedicineScrap> findByUserId(Long userId);

    // 특정 스크랩 삭제 (유저 + 약물 기준)
    void deleteByUserIdAndMedicineId(Long userId, Long medicineId);
}
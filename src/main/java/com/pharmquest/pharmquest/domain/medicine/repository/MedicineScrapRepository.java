package com.pharmquest.pharmquest.domain.medicine.repository;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.mypage.data.MedicineScrap;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineScrapRepository extends JpaRepository<MedicineScrap, Long> {
    /*특정 유저의 모든 스크랩한 약물 조회 */
    List<MedicineScrap> findByUser(User user);

    /*특정 유저가 특정 약물을 스크랩했는지 확인 */
    Optional<MedicineScrap> findByUserAndMedicine(User user, Medicine medicine);

    /* 특정 유저가 특정 약물을 이미 스크랩했는지 여부 확인 */
    boolean existsByUserAndMedicine(User user, Medicine medicine);

    @Query("SELECT ms FROM MedicineScrap ms JOIN ms.medicine m WHERE ms.user.id = :userId AND (:country IS NULL OR m.country = :country)")
    Page<MedicineScrap> findMedicineByUserIdAndCountry(@Param("userId") Long userId, @Param("country") String country, Pageable pageable);

}

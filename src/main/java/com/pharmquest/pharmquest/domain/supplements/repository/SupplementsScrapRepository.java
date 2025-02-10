package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplementsScrapRepository extends JpaRepository<SupplementsScrap, Long> {
    Optional<SupplementsScrap> findByUserAndSupplements(User user, Supplements supplements);
    boolean existsByUserIdAndSupplementsId(Long userId, Long supplementsId);

    @Query("SELECT sc.supplements.id FROM SupplementsScrap sc WHERE sc.user.id = :userId")
    List<Long> findSupplementsIdByUserId(@Param("userId") Long userId);

}
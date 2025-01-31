package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplementsScrapRepository extends JpaRepository<SupplementsScrap, Long> {
    Optional<SupplementsScrap> findByUserAndSupplements(User user, Supplements supplements);
    boolean existsByUserIdAndSupplementsId(Long userId, Long supplementsId);
    Page<SupplementsScrap> findSupplementsByUserId(Long userId, Pageable pageable);
}
package com.pharmquest.pharmquest.domain.medicine.repository;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedRepository extends JpaRepository<Medicine, Long> {
    boolean existsBySplSetId(String splSetId);
    Optional<Medicine> findBySplSetId(String splSetId);
    Page<Medicine> findByCategoryIgnoreCase(String category, Pageable pageable);
    @Query("SELECT m FROM Medicine m WHERE LOWER(m.brandName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.indicationsAndUsage) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Medicine> findByKeyword(String keyword, Pageable pageable);

    @Query("SELECT m FROM Medicine m WHERE LOWER(m.category) = LOWER(:category) " +
            "AND (LOWER(m.brandName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.indicationsAndUsage) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Medicine> findByCategoryAndKeyword(String category, String keyword, Pageable pageable);

}

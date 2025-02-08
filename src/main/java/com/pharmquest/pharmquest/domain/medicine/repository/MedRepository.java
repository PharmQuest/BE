package com.pharmquest.pharmquest.domain.medicine.repository;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedRepository extends JpaRepository<Medicine, Long> {
    boolean existsBySplSetId(String splSetId);
    Optional<Medicine> findBySplSetIdIgnoreCase(String splSetId);
    Page<Medicine> findByCategory(MedicineCategory category, Pageable pageable);
    @Query("SELECT m FROM Medicine m WHERE LOWER(m.brandName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.indicationsAndUsage) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Medicine> findByKeyword(String keyword, Pageable pageable);

    @Query("SELECT m FROM Medicine m WHERE LOWER(m.category) = LOWER(:category) " +
            "AND (LOWER(m.brandName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.indicationsAndUsage) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Medicine> findByCategoryAndKeyword(MedicineCategory category, String keyword, Pageable pageable);


}

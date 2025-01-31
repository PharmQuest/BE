package com.pharmquest.pharmquest.domain.medicine.repository;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedRepository extends JpaRepository<Medicine, Long> {
    boolean existsBySplSetId(String splSetId);
    Optional<Medicine> findBySplSetId(String splSetId);
    Page<Medicine> findByCategoryIgnoreCase(String category, Pageable pageable);
}

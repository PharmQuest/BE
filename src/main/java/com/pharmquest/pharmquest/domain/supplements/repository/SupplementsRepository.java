package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.supplements.domain.Supplements;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplementsRepository extends JpaRepository<Supplements, Long> {
}
package com.pharmquest.pharmquest.domain.supplements.service;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.enums.Nation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface SupplementsService {
    Page<Supplements> getSupplements(String category, Pageable pageable);
    Page<Supplements> searchSupplements(String keyword, Nation nation, Pageable pageable);
    boolean saveSupplements();
    Supplements getSupplementById(Long id);
}
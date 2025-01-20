package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplementsRepository extends JpaRepository<Supplements, Long> {

    // category만으로 필터링
    Page<Supplements> findByCategory4(String category4, Pageable pageable);

    // 모든 데이터 페이징
    Page<Supplements> findAll(Pageable pageable);

    Page<Supplements> findByNameContainingAndCountry(String name, Country country, Pageable pageable);
    Page<Supplements> findByNameContaining(String name, Pageable pageable);

    boolean existsByName(String name);
}
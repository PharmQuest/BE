package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplementsRepository extends JpaRepository<Supplements, Long> {

    @Query("""
    SELECT DISTINCT s FROM Supplements s
    JOIN SupplementsCategory sc ON s.id = sc.supplement.id
    WHERE sc.category.id IN (
        SELECT sc2.category.id 
        FROM SupplementsCategory sc2 
        WHERE sc2.supplement.id = :supplementId
    )
    AND s.id != :supplementId
""")
    List<Supplements> findRelatedSupplements(@Param("supplementId") Long supplementId, Pageable pageable);

    // category만으로 필터링
    Page<Supplements> findByIdIn(List<Long> ids, Pageable pageable);


    // 모든 데이터 페이징
    Page<Supplements> findAll(Pageable pageable);

    Page<Supplements> findByNameContainingAndCountry(String name, Country country, Pageable pageable);
    Page<Supplements> findByNameContaining(String name, Pageable pageable);

    boolean existsByName(String name);
}
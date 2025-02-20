package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementsRepository extends JpaRepository<Supplements, Long> {

    // 연관 영양제 조회(카테고리 기준)
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

    // 연관 영양제 조회(국가 기준)
    @Query("""
    SELECT s FROM Supplements s
    WHERE s.country = (SELECT country FROM Supplements WHERE id = :supplementId)
    AND s.id NOT IN :excludeIds
    ORDER BY RAND()
""")
    List<Supplements> findRandomSupplementsByCountry(
            @Param("supplementId") Long supplementId,
            @Param("excludeIds") List<Long> excludeIds,
            Pageable pageable
    );

    // category만으로 필터링
    Page<Supplements> findByIdIn(List<Long> ids, Pageable pageable);

    // 모든 데이터 페이징
    Page<Supplements> findAll(Pageable pageable);

    Page<Supplements> findByNameContainingAndCountry(String name, Country country, Pageable pageable);
    Page<Supplements> findByNameContaining(String name, Pageable pageable);

    // 스크랩 영양제 조회 관련 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Supplements s where s.id = :id")
    Optional<Supplements> findByIdWithPessimisticLock(@Param("id") Long id);

    // 스크랩 카운드 업데이트
    @Query("UPDATE Supplements s SET s.scrapCount = s.scrapCount + :delta WHERE s.id = :id")
    @Modifying
    void updateScrapCount(@Param("id") Long id, @Param("delta") int delta);

    boolean existsByName(String name);
}
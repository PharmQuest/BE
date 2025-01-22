package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplementsCategoryRepository extends JpaRepository<SupplementsCategory, Long> {
    @Query("SELECT sc.supplement.id FROM SupplementsCategory sc WHERE sc.category.name = :categoryName")
    List<Long> findSupplementIdByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT c.name FROM Category c JOIN SupplementsCategory sc ON c.id = sc.category.id WHERE sc.supplement.id = :supplementId")
    List<String> findCategoryNamesBySupplementId(@Param("supplementId") Long supplementId);
}
package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.supplements.data.Enum.CategoryKeyword;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplementsScrapRepository extends JpaRepository<SupplementsScrap, Long> {
    Optional<SupplementsScrap> findByUserAndSupplements(User user, Supplements supplements);
    boolean existsByUserIdAndSupplementsId(Long userId, Long supplementsId);


    @Query("SELECT DISTINCT sc FROM SupplementsScrap sc " +
            "JOIN FETCH sc.supplements s " +
            "JOIN FETCH s.supplementsCategoryList scat " +
            "JOIN FETCH scat.category c " +
            "WHERE sc.user.id = :userId")
    Page<SupplementsScrap> findByUserIdWithSupplementsAndCategories(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT DISTINCT ss FROM SupplementsScrap ss " +
            "JOIN FETCH ss.supplements s " +
            "JOIN FETCH s.supplementsCategoryList sc " +
            "JOIN FETCH sc.category c " +
            "WHERE ss.user.id = :userId AND c.name = :#{#category.name()}",
            countQuery = "SELECT COUNT(DISTINCT ss) FROM SupplementsScrap ss " +
                    "JOIN ss.supplements s " +
                    "JOIN s.supplementsCategoryList sc " +
                    "JOIN sc.category c " +
                    "WHERE ss.user.id = :userId AND c.name = :#{#category.name()}")
    Page<SupplementsScrap> findByUserIdAndCategoryWithSupplementsAndCategories(
            @Param("userId") Long userId,
            @Param("category") CategoryKeyword category,
            Pageable pageable
    );
}
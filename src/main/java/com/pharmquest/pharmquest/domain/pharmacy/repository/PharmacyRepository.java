package com.pharmquest.pharmquest.domain.pharmacy.repository;

import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    public Boolean existsByPlaceId(String placeId);

    @Query(value = "SELECT * FROM pharmacy WHERE place_id IN (:placeIds)", nativeQuery = true)
    List<Pharmacy> findAllByPlaceIds(@Param("placeIds") List<String> placeIds);


}

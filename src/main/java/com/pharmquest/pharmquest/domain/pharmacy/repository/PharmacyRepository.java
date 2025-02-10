package com.pharmquest.pharmquest.domain.pharmacy.repository;

import com.pharmquest.pharmquest.domain.pharmacy.data.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    public Boolean existsByPlaceId(String placeId);

}

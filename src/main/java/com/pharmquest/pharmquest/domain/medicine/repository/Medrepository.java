package com.pharmquest.pharmquest.domain.medicine.repository;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Medrepository extends JpaRepository<Medicine, Long> {

}

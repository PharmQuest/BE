package com.pharmquest.pharmquest.domain.supplements.repository;

import com.pharmquest.pharmquest.domain.supplements.data.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByName(String name);
}
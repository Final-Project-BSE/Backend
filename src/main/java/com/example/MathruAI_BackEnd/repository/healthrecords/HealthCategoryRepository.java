package com.example.MathruAI_BackEnd.repository.healthrecords;

import com.example.MathruAI_BackEnd.entity.healthrecords.HealthCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HealthCategoryRepository extends JpaRepository<HealthCategory, UUID> {
    Optional<HealthCategory> findBySlug(String slug);
}

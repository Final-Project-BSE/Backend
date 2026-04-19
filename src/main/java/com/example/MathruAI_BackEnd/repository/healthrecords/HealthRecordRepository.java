package com.example.MathruAI_BackEnd.repository.healthrecords;

import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.healthrecords.HealthCategory;
import com.example.MathruAI_BackEnd.entity.healthrecords.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, UUID> {

    List<HealthRecord> findByUserAndCategory(User user, HealthCategory category);

    Optional<HealthRecord> findByIdAndUser(UUID id, User user);
}
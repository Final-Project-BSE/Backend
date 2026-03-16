package com.example.MathruAI_BackEnd.repository.healthrecords;

import com.example.MathruAI_BackEnd.entity.healthrecords.HealthCategory;
import com.example.MathruAI_BackEnd.entity.healthrecords.HealthRecord;
import com.example.MathruAI_BackEnd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, UUID> {
    List<HealthRecord> findByUserAndCategory(User user, HealthCategory category);
    List<HealthRecord> findByUser(User user);
}

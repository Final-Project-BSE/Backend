package com.example.MathruAI_BackEnd.repository.reproductive.HealthMonitoring;

import com.example.MathruAI_BackEnd.entity.reproductive.healthmonitoring.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Integer> {
}

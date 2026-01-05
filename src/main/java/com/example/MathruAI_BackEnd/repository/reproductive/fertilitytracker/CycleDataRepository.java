package com.example.MathruAI_BackEnd.repository.reproductive.fertilitytracker;

import com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker.CycleData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CycleDataRepository extends JpaRepository<CycleData, Long> {
    Optional<CycleData> findTopByUserIdOrderByIdDesc(Long userId);

}




package com.example.MathruAI_BackEnd.repository.recoveryTracking;

import com.example.MathruAI_BackEnd.entity.recoveryTracking.RecoveryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecoveryRecordRepository extends JpaRepository<RecoveryRecord, Long> {
    Optional<RecoveryRecord> findByPatientIdAndDayNumber(Long patientId, Integer dayNumber);
    List<RecoveryRecord> findByPatientIdOrderByDayNumberAsc(Long patientId);
}

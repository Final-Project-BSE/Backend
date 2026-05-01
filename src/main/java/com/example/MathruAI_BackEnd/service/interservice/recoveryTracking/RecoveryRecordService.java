package com.example.MathruAI_BackEnd.service.interservice.recoveryTracking;

import com.example.MathruAI_BackEnd.dto.recoveryTracking.RecoveryRecordRequestDto;
import com.example.MathruAI_BackEnd.dto.recoveryTracking.RecoveryRecordResponseDto;

import java.util.List;

public interface RecoveryRecordService {
    RecoveryRecordResponseDto saveOrUpdateRecord(RecoveryRecordRequestDto requestDto);
    RecoveryRecordResponseDto getRecordByPatientAndDay(Long patientId, Integer dayNumber);
    List<RecoveryRecordResponseDto> getAllRecordsForPatient(Long patientId);
}

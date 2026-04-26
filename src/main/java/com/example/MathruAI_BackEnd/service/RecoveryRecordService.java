package com.example.MathruAI_BackEnd.service;

import com.example.MathruAI_BackEnd.dto.recovery.RecoveryRecordRequestDto;
import com.example.MathruAI_BackEnd.dto.recovery.RecoveryRecordResponseDto;

import java.util.List;

public interface RecoveryRecordService {
    RecoveryRecordResponseDto saveOrUpdateRecord(RecoveryRecordRequestDto requestDto);
    RecoveryRecordResponseDto getRecordByPatientAndDay(Long patientId, Integer dayNumber);
    List<RecoveryRecordResponseDto> getAllRecordsForPatient(Long patientId);
}
